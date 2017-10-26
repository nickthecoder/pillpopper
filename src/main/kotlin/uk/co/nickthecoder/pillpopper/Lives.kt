package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.animation.Grow

class Lives : ActionRole() {

    var lifeIndicators = mutableListOf<Actor>()

    override fun activated() {
        super.activated()
        for (i in 0..PillPopper.instance.lives - 1) {
            val lifeIndicator = Actor(actor.costume)
            actor.costume.choosePose("indicator")?.let { lifeIndicator.changeAppearance(it) }
            lifeIndicator.x = actor.x + i * 40
            lifeIndicator.y = actor.y
            actor.stage?.add(lifeIndicator)

            lifeIndicators.add(lifeIndicator)
        }
    }

    fun playerDied() {
        if (lifeIndicators.isNotEmpty()) {
            val item = lifeIndicators.removeAt(lifeIndicators.size - 1)
            action = Grow(item, 1.0, 0.1).and(Fade(item.color, 1.0, 0f))
        }
    }

}
