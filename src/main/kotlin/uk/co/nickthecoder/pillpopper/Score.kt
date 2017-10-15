package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole

class Score : AbstractRole() {

    override fun tick() {}

    fun update(score: Int) {
        actor.textAppearance?.text = score.toString()
    }
}
