package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.util.Attribute

class BonusFactory : ActionRole() {

    @Attribute
    var period: Double = 1.0

    @Attribute
    var quantity: Int = 3

    override fun createAction(): Action? {
        actor.hide()
        println( "Factort $period $quantity")
        return PeriodicFactory(period, quantity) {
            val block = Play.instance.neighbourhood.getBlock(actor.x, actor.y)
            if (block.occupants.isEmpty()) {
                actor.createChild("bonus")
            }
        }
    }
}