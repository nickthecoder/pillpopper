package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.CostumeAttribute

open class Pill : AbstractRole() {

    @CostumeAttribute
    var points: Int = 1

    var eaten = false

    lateinit var director: Maze

    override fun activated() {
        director = Game.instance.director as Maze
        director.neighbourhood.getBlock(actor.x, actor.y).add(this)
        director.pills++
    }

    override fun tick() {}

    open fun eat() {
        actor.die()
        eaten = true
        director.neighbourhood.getBlock(actor.x, actor.y).remove(this)
        PillPopper.instance.addPoints(points)
    }

    override fun end() {
        super.end()
    }
}
