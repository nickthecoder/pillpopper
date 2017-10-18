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

        /*
        neighbourhood.debug()
        for (x in 0..neighbourhood.blocksAcross()) {
            for (y in 0..neighbourhood.blocksDown()) {
                println("$x,$y")
                println(" 0,0      : ${neighbourhood.getBlock(neighbourhood.blockWidth * x, neighbourhood.blockWidth * y)}")
                println("-0.1,   0 : ${neighbourhood.getBlock(neighbourhood.blockWidth * x - .1, neighbourhood.blockWidth * y)}")
                println("+0.1,   0 : ${neighbourhood.getBlock(neighbourhood.blockWidth * x + .1, neighbourhood.blockWidth * y)}")
                println(" 0,  -0.1 : ${neighbourhood.getBlock(neighbourhood.blockWidth * x, neighbourhood.blockWidth * y - 0.1)}")
                println(" 0,  +0.1 : ${neighbourhood.getBlock(neighbourhood.blockWidth * x, neighbourhood.blockWidth * y + 0.1)}")
                println("")
            }
        }
        */
    }

    companion object {
        lateinit var instance: Play
    }

}
