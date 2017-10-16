package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.util.Attribute

class BonusFactory : ActionRole() {

    /**
     * Note, the first bonus will appear after initialDelay + period
     */
    @Attribute
    var initialDelay: Double = 10.0

    @Attribute
    var period: Double = 15.0

    @Attribute
    var quantity: Int = 3

    override fun createAction(): Action? {
        actor.hide()
        return Delay(initialDelay).then(
                PeriodicFactory(period, quantity) {
                    val block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)
                    if (block.occupants.isEmpty()) {
                        actor.createChild("bonus")
                    }
                }
        )
    }
}