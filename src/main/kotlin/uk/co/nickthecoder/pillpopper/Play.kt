package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood
import uk.co.nickthecoder.tickle.util.Attribute

class Play : AbstractDirector() {

    @Attribute
    var nextScene: String = ""

    val neighbourhood = StandardNeighbourhood(GRID_SIZE.toDouble())

    override fun begin() {
        Game.instance.mergeScene("glass")
        instance = this
    }

    override fun activated() {

        val width = (neighbourhood.width() + neighbourhood.blockWidth).toInt()
        val height = (neighbourhood.height() + neighbourhood.blockHeight).toInt()

        Game.instance.window.resize(width, height)
        Game.instance.scene.layout(width, height)
    }

    companion object {
        lateinit var instance: Play
    }

}
