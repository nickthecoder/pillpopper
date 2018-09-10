package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.stage.findRoles

class PowerPill : Pill() {

    override val isPowerPill = true

    override fun eaten() {
        actor.stage?.findRoles<Ghost>()?.forEach { ghost ->
            ghost.runAway()
        }
        super.eaten()
    }

}
