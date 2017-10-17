package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.util.CostumeAttribute

open class Pill : AbstractRole(), Edible {

    @CostumeAttribute
    var points: Int = 1

    var eaten = false

    open val isPowerPill = false

    override fun activated() {
        Play.instance.neighbourhood.getBlock(actor.x, actor.y).add(this)
        PillPopper.instance.pills++
    }

    override fun tick() {}

    override fun eaten() {
        actor.die()
        eaten = true
        Play.instance.neighbourhood.getBlock(actor.x, actor.y).remove(this)
        PillPopper.instance.eatenPill(isPowerPill)
    }

    override fun end() {
        super.end()
    }
}
