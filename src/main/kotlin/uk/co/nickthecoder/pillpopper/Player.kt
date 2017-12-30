package uk.co.nickthecoder.pillpopper

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute

class Player : Traveller() {

    @Attribute
    var highSpeed: Double = 4.0

    @Attribute
    var lowSpeed: Double = 3.0

    val left = Resources.instance.inputs.find("left")
    val right = Resources.instance.inputs.find("right")
    val up = Resources.instance.inputs.find("up")
    val down = Resources.instance.inputs.find("down")

    var dead: Boolean = false

    /**
     * The starting position. Go back here when we die, and resurrect.
     */
    var initialPosition = Vector2d()

    override fun begin() {
        Player.nullableInstance = this
    }

    override fun end() {
        Player.nullableInstance = null
    }

    override fun activated() {
        super.activated()
        initialPosition.set(actor.position)

        speed = highSpeed
        movement = Movement()
    }

    override fun tick() {
        super.tick()

        block.occupants.forEach { role ->
            if (role is Edible) {
                role.eaten()
                if (role is PowerPill) {
                    val talk = actor.createChildOnStage("talk")
                    (talk.role as Talk).event("powerPill")
                }
                speed = lowSpeed
            } else {
                speed = highSpeed
            }
        }
    }

    fun canMove(dir: Direction): Boolean {
        if (dir == Direction.NONE) {
            return false
        }
        return block.neighbour(dir)?.hasInstance<Solid>() != true
    }

    /**
     * A ghost has touched us.
     * We do NOT kill the Actor, because ghosts will still chase after us, and check if we are touching etc.
     */
    fun killed() {
        if (!dead) {
            dead = true

            val die = Scale(actor, 0.5, 0.1, Eases.easeIn).then { actor.hide() }

            val resurrect = Do {
                actor.position.set(initialPosition)
                travelled = 0.0
                direction = Direction.NONE
                block = findBlock()
                actor.event("default")

            }.then(Scale(actor, 0.5, 1.0))
                    .then {
                        dead = false
                    }.then(Movement())

            if (PillPopper.instance.playerDied()) {
                movement = die.then(Delay(RESTART_PERIOD - 1.0)).then(resurrect)
            } else {
                movement = die
            }
        }
    }

    /**
     * Called by PillPopper Producer when all pills have been eaten
     */
    fun levelComplete() {
        actor.event("complete")
        actor.zOrder = 100.0
        val time = 0.2

        val jumpUp = Forwards(actor.position, 50.0, Angle.degrees(90.0), time, Eases.easeOutCubic)
        val jumpDown = Forwards(actor.position, 50.0, Angle.degrees(-90.0), time, Eases.easeInCubic)
        val kissIn = Scale(actor, time, 3.0, Eases.easeIn)
        val kissOut = Scale(actor, time, 1.0, Eases.linear)

        movement = (jumpUp.and(kissIn)).then(jumpDown.and(kissOut)).repeat(3).then { Game.instance.startScene(Play.instance.nextScene) }
    }

    var nextDirection: Direction = Direction.NONE

    inner class Movement : MoveForwards() {

        override fun act(): Boolean {

            // If reversing, then do that straight away.
            // Remember which way we want to move (in nextDirection), which is then acted upon as soon as possible.
            // e.g. If going north, and pressed left, then move north till we arrive at a place where we can move west.
            if (left?.isPressed() == true) {
                if (direction == Direction.EAST) {
                    reverse()
                }
                nextDirection = Direction.WEST
            }
            if (right?.isPressed() == true) {
                if (direction == Direction.WEST) {
                    reverse()
                }
                nextDirection = Direction.EAST
            }
            if (down?.isPressed() == true) {
                if (direction == Direction.NORTH) {
                    reverse()
                }
                nextDirection = Direction.SOUTH
            }
            if (up?.isPressed() == true) {
                if (direction == Direction.SOUTH) {
                    reverse()
                }
                nextDirection = Direction.NORTH
            }

            val movedByAWholeBlock = direction == Direction.NONE || super.act()

            if (movedByAWholeBlock) {

                super.begin() // Reset for a new block movement.

                if (block.hasInstance<Tunnel>()) {

                    enterTunnel()

                } else {

                    if (canMove(nextDirection)) {
                        direction = nextDirection
                    } else {
                        if (direction != Direction.NONE && !canMove(direction)) {
                            direction = Direction.NONE
                        }
                    }
                }
            }

            return false
        }
    }

    companion object {
        var nullableInstance: Player? = null

        // This acts similar to a "lateinit var", but can also be set BACK to null later.
        // In other words, it acts like old fashioned Java - throwing null pointer exceptions when things aren't right.
        // In this case I think it's a good approach. It keeps the code looking tidy; no "!!","?.". littering the code.
        // If a scene doesn't have a Player, a NullPointerException is a good result!
        val instance: Player
            get() = nullableInstance!!
    }
}

