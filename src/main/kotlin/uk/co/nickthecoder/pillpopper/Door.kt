package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole

class Door : AbstractRole(), Solid {

    override fun activated() {
        Play.instance.neighbourhood.blockAt(actor.x, actor.y).add(this)
    }

    override fun tick() {}

}