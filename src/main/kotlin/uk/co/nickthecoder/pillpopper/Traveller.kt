package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.action.OneAction
import uk.co.nickthecoder.tickle.neighbourhood.Block

abstract class Traveller : AbstractRole() {

    var direction = Direction.NONE
        set(v) {
            if (v != field && v != field.opposite()) {
                alignWithCenterOfBlock()
            }
            field = v
        }

    var speed = 0.0

    lateinit var block: Block

    /**
     * When a ghost turns scared, this is runOne.forever(). Similar for when eaten.
     * Also, when exiting a tunnel, this is the action that follow.
     */
    var nextMovement: Action? = null

    /**
     * The current movement action. This changes over time.
     */
    var movement: Action = NoAction()
        set(v) {
            v.begin()
            field = v
        }

    /**
     * The distance travelled within this block
     */
    var travelled = 0.0

    override fun activated() {
        setBlock()
    }

    fun setBlock() {
        block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)
    }

    fun findBlock(): Block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)

    override fun tick() {
        movement.act()
    }

    fun alignWithCenterOfBlock() {
        actor.x -= travelled * direction.dx
        actor.y -= travelled * direction.dy
        travelled = 0.0
    }

    fun enterTunnel() {

        // By default we use the current movement when existing the tunnel. This will change if a power pill
        // is eaten while a ghost is in a tunnel.
        nextMovement = movement

        movement = MoveForwards().then(MoveForwards()).then {

            when (direction) {
                Direction.SOUTH -> {
                    actor.y += (actor.stage?.firstView()?.rect?.height ?: 0).toDouble()
                }
                Direction.NORTH -> {
                    actor.y -= (actor.stage?.firstView()?.rect?.height ?: 0).toDouble()
                }
                Direction.WEST -> {
                    actor.x += (actor.stage?.firstView()?.rect?.width ?: 0).toDouble()
                }
                Direction.EAST -> {
                    actor.x -= (actor.stage?.firstView()?.rect?.width ?: 0).toDouble()
                }
                Direction.NONE -> {
                }
            }

        }.then(MoveForwards().then(MoveForwards())
        ).then {
            // TODO Check if this is needed
            alignWithCenterOfBlock()

            // TODO Remove this when sure there are no errors
            val expectedBlock = findBlock()
            if (block !== expectedBlock) {
                println("ERROR. ${actor} in wrong block after tunnel. $block vs $expectedBlock")
            }
            setBlock()

            movement = nextMovement ?: OneAction { println("ERROR. No next action set when entering the tunnel") }
        }

    }

    open inner class MoveForwards : Action {

        var changedBlock = false

        override fun begin(): Boolean {
            changedBlock = false
            return false
        }

        override fun act(): Boolean {
            if (travelled >= GRID_SIZE) {
                println("ERROR. Already travelled more than a whole block.")
            }
            travelled += speed
            actor.x += speed * direction.dx
            actor.y += speed * direction.dy

            // Note. Don't use 0.5, because that will land us on the boundary of two blocks, and which block gets picked
            // might be affected by rounding errors.
            if (!changedBlock && travelled > GRID_SIZE * 0.6) {
                setBlock()
                changedBlock = true
            }

            if (travelled >= GRID_SIZE) {
                travelled -= GRID_SIZE
                return true
            } else {
                return false
            }
        }

        fun reverse() {
            direction = direction.opposite()
            travelled = GRID_SIZE - travelled
            setBlock()
            changedBlock = false
        }

    }
}