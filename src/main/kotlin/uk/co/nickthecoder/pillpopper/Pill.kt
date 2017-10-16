package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.CostumeAttribute

open class Pill : AbstractRole() {

    @CostumeAttribute
    var points: Int = 1

    var eaten = false

    lateinit var director: Play

    override fun activated() {
        director = Game.instance.director as Play
        director.neighbourhood.getBlock(actor.x, actor.y).add(this)
        director.pills++
    }

    override fun tick() {}

    open fun eat() {
        actor.die()
        eaten = true
        director.neighbourhood.getBlock(actor.x, actor.y).remove(this)
        addScore()
    }

    open fun addScore() {
        PillPopper.instance.eatenPill()
    }

    override fun end() {
        super.end()
    }
}
