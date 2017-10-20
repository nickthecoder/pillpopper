package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.util.Attribute

class Play : AbstractDirector() {

    @Attribute
    var powerPillTime = 10.0

    @Attribute
    var nextScene: String = ""

    val neighbourhood = StandardNeighbourhood<Role>(GRID_SIZE.toDouble())

    var powerPillTimer: Action? = null
        set(v) {
            field = v
            v?.begin()
        }

    override fun begin() {
        instance = this
        Game.instance.mergeScene("glass")
    }

    override fun activated() {

        val width = (neighbourhood.width() + neighbourhood.blockWidth).toInt()
        val height = (neighbourhood.height() + neighbourhood.blockHeight).toInt()

        Game.instance.window.resize(width, height + INFO_HEIGHT)
        Game.instance.scene.layout(width, height)
    }

    override fun preTick() {
        powerPillTimer?.act()
    }

    fun eatenPowerPill() {
        powerPillTimer = Delay(powerPillTime - POWER_PILL_WARNING_TIME)
                .then { powerPillWarning() }
                .then(Delay(POWER_PILL_WARNING_TIME))
                .then { powerPillEnd() }
    }


    fun powerPillWarning() {
        Player.instance.actor.stage!!.findRole<Ghost>().forEach { ghost ->
            ghost.powerPillWarning()
        }
    }

    fun powerPillEnd() {
        Player.instance.actor.stage!!.findRole<Ghost>().forEach { ghost ->
            ghost.powerPillEnd()
        }
        powerPillTimer = null
    }

    companion object {
        lateinit var instance: Play
    }

}
