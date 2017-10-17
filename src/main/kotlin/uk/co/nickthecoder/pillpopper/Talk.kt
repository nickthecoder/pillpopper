package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.util.Angle

class Talk : ActionRole() {

    val direction = Angle()

    override fun createAction(): Action? {
        actor.y += 70.0
        actor.scale = 0.5
        actor.color.transparent()

        val growTime = 0.2
        val moveTime = 0.3
        val delayTime = 0.6

        val grow = Grow(actor, growTime, 1.0, Eases.easeOut)
                .and(Fade(actor.color, growTime,1f, Eases.linear))

        val move = Forwards(actor.position, 1000.0, direction, moveTime, Eases.easeIn)
                .and(Fade(actor.color, moveTime, 0f, Eases.easeIn))

        return grow.then(Delay(delayTime)).then(move).then(Kill(actor))

    }

}