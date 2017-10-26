package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.util.Attribute

class Score : AbstractRole() {

    @Attribute
    var prefix: String = "Score : "

    @Attribute
    var suffix: String = ""

    override fun tick() {}

    fun update(score: Int) {
        actor.textAppearance?.text = prefix + score + suffix
    }
}
