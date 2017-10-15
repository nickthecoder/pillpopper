package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game

class Ghost : AbstractRole() {

    override fun activated() {
        Game.instance.director.neighbourhood.getBlock(actor.x, actor.y).add(this)
    }

    override fun tick() {

    }
}