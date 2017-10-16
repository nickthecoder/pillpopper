package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.stage.findRole

class PowerPill : Pill() {

    override val isPowerPill = true

    override fun eaten() {
        super.eaten()
        actor.stage?.findRole<Ghost>()?.forEach { ghost ->
            ghost.runAway()
        }
    }

}
