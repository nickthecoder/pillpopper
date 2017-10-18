package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.neighbourhood.Block

abstract class Traveller : AbstractRole() {

    var direction = Direction.NORTH

    var speed = 0.0

    lateinit var block: Block

    /**
     * When a ghost turns scared, this is changed to "run away". Similar for when eaten.
     * Also, when exiting a tunnel, this is the action that follow.
     */
    var nextMovement: Action = NoAction()

    /**
     * The current movement action. This changes over time.
     */
    var movement: Action = NoAction()
        set(v) {
            v.begin()
            field = v
        }


    var travelled = 0.0

    override fun activated() {
        block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)
    }

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
            }
            println("Jumped")

        }.then(MoveForwards().then(MoveForwards())
        ).then {
            println("exiting tunnel $actor @ $block")

            //alignWithCenterOfBlock()

            // TODO Remove this when sure there are no errors
            val expectedBlock = Play.instance.neighbourhood.getBlock(actor.x, actor.y)
            if (block !== expectedBlock) {
                println("ERROR. ${actor} in wrong block after tunnel. $block vs $expectedBlock")
            }
            block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)

            movement = nextMovement
        }

    }

    inner class MoveForwards : Action {

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

            if (!changedBlock && travelled > GRID_SIZE * 0.5) {
                block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)
                println("Traveller changed block to $block")
                changedBlock = true
            }

            if (travelled >= GRID_SIZE) {
                travelled -= GRID_SIZE
                return true
            } else {
                return false
            }
        }

    }
}