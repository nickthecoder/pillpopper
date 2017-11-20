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

class Points : ActionRole() {

    override fun createAction(): Action {
        actor.y += 30.0
        actor.scale = 0.3
        val time = 0.5
        val stillTime = 0.2
        val growTime = 0.3
        val fadeDelay = 0.3
        val rise = Forwards(actor.position, 60.0, Angle.degrees(90.0), time - stillTime, Eases.easeOut)

        val grow = Grow(actor, growTime, 1.0, Eases.easeOut)
                .then(Grow(actor, time - growTime, 0.3, Eases.easeIn))

        val fade = Delay(fadeDelay).then(Fade(actor.color, time - fadeDelay, 1f, Eases.easeIn))

        return rise.and(grow).and(fade).then(Kill(actor))

    }

}