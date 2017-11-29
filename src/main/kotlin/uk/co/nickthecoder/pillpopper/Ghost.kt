package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.action.Idle
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.stage.findRoles
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute

abstract class Ghost : Traveller() {

    /**
     * Initial delay at the start of the scene. Each Ghost will have a different delay, so they don't end up on
     * top of each other when travelling at the same speed.
     */
    @CostumeAttribute
    var initialIdle = 0

    @Attribute
    var highSpeed = 4.0

    @Attribute
    var lowSpeed = 3.0

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
    val chaseOne = Do { changeDirection(chaseScorer) }
            .then(CheckTouching().whilst(MoveForwards()))

    /**
     * Chases forever - [movement] uses this most of the time.
     */
    val chase: Action = Do { canBeScared = true }.then(chaseOne.forever())

    /**
     * A run-away movement of one block
     */
    val runOne = Do { changeDirection { runAwayScorer(it) } }
            .then(CheckTouching().whilst(MoveForwards()))

    /**
     * Set when the eaten ghost has gone through the door. Reset when the delay is over, and the ghost is
     * seeking the door to escape.
     */
    var waitingInPen = true

    /**
     * Used to alter the test for canMove, to only allow access to a door when exiting or entering the pen.
     * Not when chasing, or running away. Also used to prevent ghosts turning scared while in the pen.
     */
    var seekingDoor: Boolean = false

    /**
     * Set to true when chasing the player.
     * Set to false when eaten
     */
    var canBeScared: Boolean = false

    /**
     * True after a power pill has been eaten. Reset when the pill was worn off, or I've been eaten
     */
    var scared: Boolean = false

    /**
     * true when the ghost has been eaten. Reset when out of the pen.
     * Used when touching the player (touching does nothing when eaten).
     */
    var eaten: Boolean = false

    var powerPillWarningAction: Action? = null
        set(v) {
            field = v
            v?.begin()
        }

    lateinit var door: Role

    override fun activated() {
        super.activated()

        speed = highSpeed
        actor.color = Color(1f, 1f, 1f, 0.8f)
        waitingInPen = true
        movement = Idle(initialIdle)
                .then(chaseOne.repeat(exitAfter))
                .then { waitingInPen = false }.then(seekDoorAction(afterAction = chase))

        val foundDoor = closest(actor.stage!!.findRoles<Door>())
        if (foundDoor == null) {
            actor.die()
            println("ERROR. No door found!")
        } else {
            door = foundDoor
        }
    }

    override fun tick() {
        super.tick()
        powerPillWarningAction?.act()
    }

    /**
     * After moving a whole block, decide which way to move next.
     * If nextMovement is set, then this different pattern of movement is started (by setting movement).
     */
    fun changeDirection(scorer: (Direction) -> Double) {
        val next = nextMovement
        if (next != null) {
            nextMovement = null
            movement = next
            movement.act()
            return
        }

        // For debugging only
        if (block !== findBlock()) {
            println("ERROR. Ghost is in the wrong block $block vs ${findBlock()} $actor")
        }

        if (block.hasInstance<Tunnel>()) {
            speed = lowSpeed
            enterTunnel()
        } else {
            direction = chooseDirection(scorer)
        }
    }

    fun chooseDirection(scorer: (Direction) -> Double): Direction {

        var bestScore = -Double.MAX_VALUE
        var bestDirection: Direction? = null

        Direction.values().forEach { dir ->
            if (dir != Direction.NONE && canMove(dir) && !dir.isOpposite(direction)) {
                val score = scorer(dir)
                if (score > bestScore) {
                    bestScore = score
                    bestDirection = dir
                }
            }
        }
        if (bestDirection == null) {
            // A dead end - turn around!
            return direction.opposite()
        } else {
            return bestDirection!!
        }
    }

    fun canMove(dir: Direction): Boolean {
        val neighbour = block.neighbour(dir)
        val isSolid = neighbour?.hasInstance<Solid>() != false

        if (isSolid) {
            if (seekingDoor) {
                return neighbour?.hasInstance<Door>() == true
            } else {
                return false
            }
        } else {
            return true
        }
    }

    /**
     * Run away from the player
     */
    fun runAwayScorer(dir: Direction): Double {
        return -scoreDirectlyTo(dir, Player.instance.actor.x, Player.instance.actor.y)
    }

    /**
     * Head for x,y
     */
    fun scoreDirectlyTo(dir: Direction, targetX: Double, targetY: Double): Double {
        return if (dir.dx == 0) {
            (targetY - actor.y) * dir.dy
        } else {
            (targetX - actor.x) * dir.dx
        }
    }

    /**
     * A power pill has been eaten by the Player.
     */
    fun runAway() {
        if (canBeScared) {
            actor.event("scared")
            speed = lowSpeed
            scared = true

            nextMovement = runOne.until { !eaten && !scared }
                    .then {
                        actor.event("default")
                        movement = chase
                    }
        }
    }

    /**
     * Caught by Player after a power pill has been eaten.
     */
    fun eaten() {
        actor.event("eaten") // Change appearance
        speed = highSpeed
        eaten = true
        scared = false
        canBeScared = false
        powerPillWarningAction = null

        val points = actor.createChildOnStage("points")
        points.textAppearance?.text = PillPopper.instance.eatenGhost().toString()

        val inPen = chaseOne.repeat(2)
                // Change back to a normal ghost
                .then {
                    waitingInPen = true
                    eaten = false
                    actor.event("default")
                }
                .then(
                        chaseOne.repeat(reExitAfter) // Wait in the pen for a while,
                                // Head out the door and resume chasing the player
                                .then { waitingInPen = false }
                                .then(seekDoorAction(afterAction = chase))
                )

        nextMovement = seekDoorAction(inPen)
    }

    /**
     * Avoid the player, until PillPopper tells us to chase after him again.
     */
    fun playerDied() {
        if (!eaten && !scared && !seekingDoor && !waitingInPen) {
            nextMovement = runOne.forSeconds(RESTART_PERIOD)
                    .then { nextMovement = chase }.then(runOne)
        }
    }

    fun seekDoorAction(afterAction: Action): Action {

        val scorer = { dir: Direction -> scoreDirectlyTo(dir, door.actor.x, door.actor.y) }

        return Do { seekingDoor = true }
                .then { changeDirection(scorer) }
                .then(MoveForwards())
                .then {
                    if (block.hasInstance<Door>()) {
                        seekingDoor = false
                        movement = afterAction
                    }
                }.forever()
    }

    fun touchingPlayer(): Boolean {
        val dx = Math.abs(Player.instance.actor.x - actor.x)
        val dy = Math.abs(Player.instance.actor.y - actor.y)

        return dx < TOUCHING_DISTANCE && dy < TOUCHING_DISTANCE
    }

    fun levelComplete() {
        movement = NoAction()
    }

    fun powerPillWarning() {
        if (scared) {
            val flash = Do { actor.event("default") }
                    .then(Idle(6))
                    .then { actor.event("scared") }
                    .then(Idle(20))

            powerPillWarningAction = flash.until { !scared }.then { powerPillWarningAction = null }
        }
    }

    fun powerPillEnd() {
        scared = false
    }

    inner class CheckTouching : Action {
        override fun act(): Boolean {

            if (touchingPlayer()) {
                if (eaten) {
                    // Do nothing
                } else if (scared) {
                    eaten()
                } else {
                    Player.instance.killed()
                }
            }
            return true
        }
    }
}


