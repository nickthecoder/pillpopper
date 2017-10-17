package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood
import uk.co.nickthecoder.tickle.util.Attribute

class Play : AbstractDirector() {

    @Attribute
    var nextScene: String = ""

    override val neighbourhood = StandardNeighbourhood(GRID_SIZE.toDouble())

    override fun begin() {
        Game.instance.mergeScene("glass")
        instance = this
    }

    companion object {
          lateinit var instance: Play
    }

}
