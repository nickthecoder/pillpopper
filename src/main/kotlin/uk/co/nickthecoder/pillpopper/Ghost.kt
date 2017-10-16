package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Idle
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.action.OneAction
import uk.co.nickthecoder.tickle.neighbourhood.Block
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute

abstract class Ghost : AbstractRole() {

    var direction = Direction.NORTH

    /**
     * Initial delay at the start of the scene. Each Ghost will have a different delay, so they don't end up on
     * top of each other when travelling at the same speed.
     */
    @CostumeAttribute
    var initialIdle = 0

    @Attribute
    var speed = 4.0

    /**
     * Time to stay in the pen at the beginning of the scene (measured in whole-block movements)
     */
    @Attribute
    var exitAfter = 5

    /**
     * Time to stay in the pen after being eaten (measured in whole-block movements)
     */
    @Attribute
    var reExitAfter = 5

    /**
     * Each ghost has their own personality. Winky heads directly to the player, Stinky heads to where the player
     * is heading etc.
     */
    abstract val chaseScorer: (Direction) -> Double

    /**
     * A chasing movement of one block
     */
    val chaseOne = OneAction { changeDirection(chaseScorer) }.then(MoveForwards().then { checkTurningScared() })

    /**
     * Chases forever - [movement] uses this most of the time.
     */
    val chase: Action = chaseOne.forever()

    /**
     * The current movement action. This changes over time.
     */
    var movement: Action = NoAction()
        set(v) {
            v.begin()
            field = v
        }

    var travelled = 0.0


    lateinit var block: Block

    var seekingDoor: Boolean = false

    /**
     * True when a power pill has been eaten. Reset as soon as checkTurningScared has run.
     */
    var turningScared: Boolean = false

    /**
     * True after a power pill has been eaten. Reset when the pill was worn off, of I've been eaten
     */
    var scared: Boolean = false

    /**
     * true when the ghost has been eaten. Reset when out of the pen.
     */
    var eaten: Boolean = false

    lateinit var door: Role

    override fun activated() {
        movement = Idle(initialIdle).then(chaseOne.repeat(exitAfter).then { seekDoor(afterAction = chase) })

        block = Game.instance.director.neighbourhood.getBlock(actor.x, actor.y)
        val foundDoor = closest(actor.stage!!.findRole<Door>())
        if (foundDoor == null) {
            actor.die()
            println("ERROR. No door found!")
        } else {
            door = foundDoor
        }
    }

    override fun tick() {
        movement.act()
    }

    fun changeDirection(scorer: (Direction) -> Double) {
        val oldDirection = direction
        travelled = travelled.rem(GRID_SIZE)

        block = Game.instance.director.neighbourhood.getBlock(actor.x, actor.y)

        chooseDirection(scorer)

        // Correct for overshoot when turning a corner.
        if (oldDirection != direction) {
            actor.x -= travelled * oldDirection.dx
            actor.y -= travelled * oldDirection.dy
        }
    }

    fun chooseDirection(scorer: (Direction) -> Double) {

        var bestScore = -Double.MAX_VALUE
        var bestDirection: Direction? = null

        Direction.values().forEach { dir ->
            if (canMove(dir) && !dir.isOpposite(direction)) {
                val score = scorer(dir)
                if (score > bestScore) {
                    bestScore = score
                    bestDirection = dir
                }
            }
        }
        if (bestDirection == null) {
            actor.die()
        } else {
            direction = bestDirection!!
        }
    }

    fun canMove(direction: Direction) = !block.isSolid(direction.dx, direction.dy, seekingDoor)

    /**
     * Run away from the player
     */
    fun runAwayScorer(dir: Direction): Double {
        return -scoreDirectlyTo(dir, Player.instance.actor.x, Player.instance.actor.y)
    }

    /**
     * Head for x,y
     */
    fun scoreDirectlyTo(dir: Direction, tartgetX: Double, targetY: Double): Double {
        return if (dir.dx == 0) {
            (targetY - actor.y) * dir.dy
        } else {
            (tartgetX - actor.x) * dir.dx
        }
    }

    fun seekDoor(afterAction: Action) {
        seekingDoor = true

        val scorer = { dir: Direction -> scoreDirectlyTo(dir, door.actor.x, door.actor.y) }

        movement = OneAction { changeDirection(scorer) }.then(MoveForwards().then {
            if (block.isDoor()) {
                seekingDoor = false
                movement = afterAction
            }
        }).forever()

    }

    /**
     * A power pill has been eaten by the Player.
     */
    fun runAway() {
        if (!eaten && !seekingDoor) {
            actor.event("scared")
            turningScared = true
            scared = true
        }
    }

    fun checkTurningScared() {
        if (turningScared) {
            turningScared = false
            val runOne = OneAction { changeDirection { runAwayScorer(it) } }.then(MoveForwards()).then {
                checkTurningScared()
                checkEaten()
            }
            movement = runOne.repeat(30)
                    .then {
                        scared = false
                        actor.event("default")
                        movement = chase
                    }
        }
    }

    fun checkEaten() {
        if (eaten) {
            turningScared = false
            // Go to the pen
            seekDoor(chaseOne.repeat(2)
                    // Change back to a normal ghost
                    .then { actor.event("default") }
                    .then(
                            chaseOne.repeat(reExitAfter) // Wait in the pen for a while,
                                    // Head out the door and resume chasing the player
                                    .then {
                                        eaten = false
                                        seekDoor(afterAction = chase)
                                    }
                    )
            )
        }
    }

    fun touchingPlayer(): Boolean {
        val dx = Math.abs(Player.instance.actor.x - actor.x)
        val dy = Math.abs(Player.instance.actor.y - actor.y)

        return dx < TOUCHING_DISTANCE && dy < TOUCHING_DISTANCE
    }

    /**
     * Caught by Player after a power pill has been eaten.
     */
    fun eaten() {
        actor.event("eaten") // Change appearance
        eaten = true
        scared = false
        PillPopper.instance.eatenGhost()
    }

    inner class MoveForwards : Action {
        override fun act(): Boolean {
            travelled += speed
            actor.x += speed * direction.dx
            actor.y += speed * direction.dy

            if (touchingPlayer()) {
                if (eaten) {
                    // Do nothing
                } else if (scared) {
                    eaten()
                } else {
                    Player.instance.killed()
                }

            }

            return travelled >= GRID_SIZE
        }

    }

}

