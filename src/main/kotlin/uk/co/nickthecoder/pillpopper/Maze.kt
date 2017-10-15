package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood

class Maze : AbstractDirector() {

    override val neighbourhood = StandardNeighbourhood(GRID_SIZE.toDouble())

    var pills : Int = 0

    override fun begin() {
        Game.instance.mergeScene("glass")
    }

}
