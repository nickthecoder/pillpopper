package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole

class Tunnel : AbstractRole() {

    override fun activated() {
        actor.hide()
        Play.instance.neighbourhood.getBlock(actor.x, actor.y).add(this)
    }

    override fun tick() {}

}
