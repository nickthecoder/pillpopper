package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole

open class Wall : AbstractRole(), Solid {

    override fun activated() {
        Play.instance.neighbourhood.getBlock(actor.x, actor.y).add(this)
    }

    override fun tick() {}
}
