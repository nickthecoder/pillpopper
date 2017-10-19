package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Kill

class Bonus : ActionRole(), Edible {

    override fun createAction(): Action? {
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).add(this)
        return Delay(10.0).then { PillPopper.instance.missedBonus() }.then(Kill(actor))
    }

    override fun eaten() {
        actor.die()
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).remove(this)
        val points = actor.createChildOnStage("points")
        points.textAppearance?.text = PillPopper.instance.eatenBonus().toString()

    }

    override fun end() {
        super.end()
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).remove(this)
    }
}
