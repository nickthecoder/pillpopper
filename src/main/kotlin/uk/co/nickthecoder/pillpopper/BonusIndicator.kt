package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor

class BonusIndicator : ActionRole() {

    override fun tick() {}

    val indicators = mutableListOf<Actor>()

    fun initialise(index: Int) {

        for (i in 0..index - 1) {
            createIndicator(i)
        }
        // Show the next bonus as semi-transparent
        actor.color.alpha = 0.3f
        actor.event(index.toString())
    }

    private fun createIndicator(i: Int) {
        val indicator = Actor(actor.costume)
        indicator.event(i.toString())
        indicator.x = actor.x
        indicator.y = actor.y
        actor.stage?.add(indicator)
        indicators.add(indicator)

        actor.x -= 40
    }

    fun bonusSequence(index: Int) {
        createIndicator(index)
        actor.costume.choosePose((index + 1).toString())?.let { actor.changeAppearance(it) }
        if (index == BONUSES - 1) {
            // We've eaten all bonus in the correct sequence. Remove the list and start again.
            indicators.forEach { it.die() }
            actor.x += indicators.size * 40
            indicators.clear()
            actor.event("0")
        }
    }
}
