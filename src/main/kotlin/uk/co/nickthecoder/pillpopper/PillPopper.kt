package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractProducer
import uk.co.nickthecoder.tickle.Game

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

    override fun sceneActivated() {
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

    fun eatenPowerPill() {
        ghostPoints = 50
        addPoints(10)
    }

    fun eatenPill() {
        addPoints(1)
    }

    private fun addPoints(points: Int) {
        score += points
        scoreRole?.update(score)
    }

    companion object {
        lateinit var instance: PillPopper
    }
}
