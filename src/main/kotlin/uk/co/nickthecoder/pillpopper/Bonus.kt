package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class Bonus : ActionRole(), Edible {

    /**
     * The index of this bonus (the order the bonuses must be eaten for maximum score).
     */
    @CostumeAttribute
    var index: Int = -1

    override fun createAction(): Action {
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).add(this)
        return Delay(10.0).then(Kill(actor))
    }

    override fun eaten() {
        actor.event("eaten")
        actor.die()
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).remove(this)
        val bonusPoints = PillPopper.instance.eatenBonus(index)
        if (bonusPoints > 0) {
            val points = actor.createChild("points")
            points.textAppearance?.text = bonusPoints.toString()
        }

    }

    override fun end() {
        super.end()
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).remove(this)
    }
}
