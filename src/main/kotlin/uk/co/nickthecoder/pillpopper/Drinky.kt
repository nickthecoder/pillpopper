package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.stage.findRole

private val TOO_CLOSE = GRID_SIZE * 8

class Drinky : Ghost() {

    var flag: Role? = null

    override fun activated() {
        super.activated()
        flag = actor.stage?.findRole<Flag>()?.firstOrNull()
    }

    /**
     * Head directly for the player, but when close, head for the bottom left corner
     */
    override val chaseScorer = { dir: Direction ->
        val pa = Player.instance.actor
        val dx = Math.abs(pa.x - actor.x)
        val dy = Math.abs(pa.y - actor.y)
        if (dx < TOO_CLOSE && dy < TOO_CLOSE) {
            if (flag != null) {
                scoreDirectlyTo(dir, flag!!.actor.x, flag!!.actor.y)
            } else {
                scoreDirectlyTo(dir, 0.0, 0.0)
            }
        } else {
            scoreDirectlyTo(dir, pa.x, pa.y)
        }
    }

}
