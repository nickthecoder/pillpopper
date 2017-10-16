package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.CostumeAttribute

open class Pill : AbstractRole() {

    @CostumeAttribute
    var points: Int = 1

    var eaten = false

    open val isPowerPill = false

    lateinit var director: Play

    override fun activated() {
        director = Game.instance.director as Play
        director.neighbourhood.getBlock(actor.x, actor.y).add(this)
        PillPopper.instance.pills++
    }

    override fun tick() {}

    open fun eaten() {
        actor.die()
        eaten = true
        director.neighbourhood.getBlock(actor.x, actor.y).remove(this)
        PillPopper.instance.eatenPill(isPowerPill)
    }

    override fun end() {
        super.end()
    }
}
