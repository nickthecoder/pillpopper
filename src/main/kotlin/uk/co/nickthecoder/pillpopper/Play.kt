package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood

class Play : AbstractDirector() {

    override val neighbourhood = StandardNeighbourhood(GRID_SIZE.toDouble())

    override fun begin() {
        Game.instance.mergeScene("glass")
    }

}
