package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractProducer
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.stage.findRole

class PillPopper : AbstractProducer() {

    init {
        instance = this
    }

    var scoreRole: Score? = null

    var score: Int = 0

    /**
     * Points for eating a scared ghost. Increases after each meal. Reset to 50 when eating a power pill.
     */
    var ghostPoints = 0

    /**
     * The number of pills yet to be collected. Incremented  by Pill.onActivate
     */
    var pills: Int = 0

    override fun sceneBegin() {
        pills = 0

        Game.instance.scene.findStage("glass")?.let { glass ->
            scoreRole = glass.actors.firstOrNull { it.role is Score }?.role as Score
            scoreRole?.update(score)
        }
    }

    fun eatenGhost(): Int {
        val points = ghostPoints
        ghostPoints *= 2
        addPoints(points)
        return points
    }

    fun eatenPill(isPowerPill: Boolean) {
        if (isPowerPill) {
            ghostPoints = 50
            addPoints(10)
        } else {
            addPoints(1)
        }
        pills--
        if (pills <= 0) {
            Player.instance.levelComplete()

            Player.instance.actor.stage!!.findRole<Ghost>().forEach { ghost ->
                ghost.movement = NoAction()
            }
        }
    }

    fun addPoints(points: Int) {
        score += points
        scoreRole?.update(score)
    }

    companion object {
        lateinit var instance: PillPopper
    }
}
