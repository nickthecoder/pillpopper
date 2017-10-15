package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractProducer
import uk.co.nickthecoder.tickle.Game

class PillPopper : AbstractProducer() {

    init {
        instance = this
    }

    var scoreRole: Score? = null

    var score: Int = 0

    override fun sceneActivated() {
        Game.instance.scene.findStage("glass")?.let { glass ->
            scoreRole = glass.actors.firstOrNull { it.role is Score }?.role as Score
            scoreRole?.update(score)
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
