package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.util.Angle

private val MARGIN = 30.0

class Talk : AbstractRole() {

    val direction = Angle()

    var movement: Action? = null
        set(v) {
            field = v
            v?.begin()
        }


    fun event(eventName: String) {
        actor.event(eventName)

        val padded = actor.appearance.width() / 2 + MARGIN
        val maxRight = (actor.stage?.firstView()?.rect?.width?.toDouble() ?: 0.0) - padded

        if (actor.x < padded) {
            actor.x = padded
        }
        if (actor.x > maxRight) {
            actor.x = maxRight
        }

        actor.y += 70.0
        actor.scaleXY = 0.5
        actor.color.transparent()

        val growTime = 0.2
        val moveTime = 0.3
        val delayTime = 0.6

        val grow = Scale(actor, growTime, 1.0, Eases.easeOut)
                .and(Fade(actor.color, growTime, 1f, Eases.linear))

        val move = Forwards(actor.position, 1000.0, direction, moveTime, Eases.easeIn)
                .and(Fade(actor.color, moveTime, 0f, Eases.easeIn))

        movement = grow.then(Delay(delayTime)).then(move).then(Kill(actor))

    }

    override fun tick() {
        movement?.act()
    }
}