package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.stage.findRole

class PowerPill : Pill() {

    override fun eat() {
        super.eat()
        actor.stage?.findRole<Ghost>()?.forEach { ghost ->
            ghost.runAway()
        }
    }

    override fun addScore() {
        PillPopper.instance.eatenPowerPill()
    }

}
