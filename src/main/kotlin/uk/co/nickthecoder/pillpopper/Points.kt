package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle

class Points : ActionRole() {

    override fun createAction(): Action? {
        actor.y += 20.0
        val riseTime = 0.5
        val shrinkTime = 5.5
        println("Created points")

        return Forwards(actor.position, 100.0, Angle.degrees(90.0), riseTime, Eases.easeIn)
                .and(Grow(actor, riseTime, 3.0, Eases.easeInCubic))
                .then(
                        Grow(actor, shrinkTime, 0.1, Eases.easeIn)
                                .and(Fade(actor, shrinkTime, Color.TRANSPARENT_WHITE, Eases.easeIn))
                )
                .then { println("Points destroyed?") }
                .then(Kill(actor))

    }

}