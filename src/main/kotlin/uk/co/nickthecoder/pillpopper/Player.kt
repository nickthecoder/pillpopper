package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.neighbourhood.Block
import uk.co.nickthecoder.tickle.resources.Resources

val GRID_SIZE = 40

class Player : AbstractRole() {

    val left = Resources.instance.input("left")
    val right = Resources.instance.input("right")
    val up = Resources.instance.input("up")
    val down = Resources.instance.input("down")

    var speed: Double = 4.0

    lateinit var block: Block

    override fun activated() {
        block = Game.instance.director.neighbourhood.getBlock(actor.x, actor.y)
    }

    val movement = Movement()

    override fun tick() {
        movement.act()

        block.occupants.forEach {
            val role = it.role
            if (role is Pill) {
                role.eat()
            }
        }
    }

    inner class Movement : Action {

        var dx: Int = 0
        var dy: Int = 0

        var nextDx: Int = 0
        var nextDy: Int = 0

        var travelled: Double = 0.0

        override fun act(): Boolean {
            // If moving in the same direction as the key press, do nothing
            // If moving in the opposite direction to the key press, reverse direction.
            // If turning left or right, remember which way we want to move in nextDx,nextDy.
            if (left.isPressed()) {
                if (dx == 1) {
                    travelled = GRID_SIZE - travelled
                    dx = -1
                }
                nextDx = -1
                nextDy = 0
            }
            if (right.isPressed()) {
                if (dx == -1) {
                    travelled = GRID_SIZE - travelled
                    dx = 1
                }
                nextDx = 1
                nextDy = 0
            }
            if (down.isPressed()) {
                if (dy == 1) {
                    travelled = GRID_SIZE - travelled
                    dy = -1
                }
                nextDx = 0
                nextDy = -1
            }
            if (up.isPressed()) {
                if (dy == -1) {
                    travelled = GRID_SIZE - travelled
                    dy = 1
                }
                nextDx = 0
                nextDy = 1
            }

            // Move
            actor.x += dx * speed
            actor.y += dy * speed

            travelled += speed * Math.abs(dx + dy)

            // Change our block
            if (travelled > 0.8 * GRID_SIZE) {
                block = Game.instance.director.neighbourhood.getBlock(actor.x, actor.y)
            }

            // Have we travelled a whole GRID_SIZE? in which case, we need to check if we should turn left or right.
            if (travelled >= GRID_SIZE || (dx == 0 && dy == 0)) {
                travelled = travelled.rem(GRID_SIZE)

                if (canMove(nextDx, nextDy)) {
                    changeDirection(nextDx, nextDy)
                } else {
                    if (!canMove(dx, dy)) {
                        dx = 0
                        dy = 0
                    }
                }
            }

            return false
        }

        fun changeDirection(deltaX: Int, deltaY: Int) {
            if (dx == deltaX && dy == deltaY) {
                // Don't need to do anything!
                return
            }

            // Turning a corner. We may have overshot the junction a little, so lets move back to compensate
            actor.x -= travelled * dx
            actor.y -= travelled * dy
            travelled = 0.0

            dx = deltaX
            dy = deltaY
        }

        fun canMove(deltaX: Int, deltaY: Int): Boolean {
            if (deltaX == 0 && deltaY == 0) {
                return false
            }
            val nextBlock = block.neighbouringBlock(deltaX, deltaY)
            nextBlock ?: return false

            nextBlock.occupants.forEach {
                if (it.role is Solid) {
                    return false
                }
            }
            return true
        }
    }
}

