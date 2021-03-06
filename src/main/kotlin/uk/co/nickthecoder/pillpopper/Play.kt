package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood
import uk.co.nickthecoder.tickle.stage.findRoles
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

    override fun sceneLoaded() {
        instance = this
        Game.instance.mergeScene("glass")
    }

    override fun activated() {
        super.activated()

        val width = (neighbourhood.width() + neighbourhood.blockWidth).toInt()
        val height = (neighbourhood.height() + neighbourhood.blockHeight).toInt() + INFO_HEIGHT

        Game.instance.window.resize(width, height)
        Game.instance.scene.layoutToFit()
    }

    override fun tick() {
        powerPillTimer?.act()
    }

    fun eatenPowerPill() {
        powerPillTimer = Delay(powerPillTime - POWER_PILL_WARNING_TIME)
                .then { powerPillWarning() }
                .then(Delay(POWER_PILL_WARNING_TIME))
                .then { powerPillEnd() }
    }


    fun powerPillWarning() {
        Player.instance.actor.stage!!.findRoles<Ghost>().forEach { ghost ->
            ghost.powerPillWarning()
        }
    }

    fun powerPillEnd() {
        Player.instance.actor.stage!!.findRoles<Ghost>().forEach { ghost ->
            ghost.powerPillEnd()
        }
        powerPillTimer = null
    }

    companion object {
        lateinit var instance: Play
    }

}
