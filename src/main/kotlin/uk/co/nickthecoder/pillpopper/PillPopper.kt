package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.AbstractProducer
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.stage.findRole

class PillPopper : AbstractProducer() {

    init {
        instance = this
    }

    var scoreRole: Score? = null

    var livesIndicator: Lives? = null

    var score: Int = 0

    /**
     * Points for eating a scared ghost. Increases after each meal. Reset to 50 when eating a power pill.
     */
    var ghostPoints = 0

    /**
     * Points for eating a Bonus. Increases for each bonus eaten.
     */
    var bonusPoints = STARTING_BONUS

    /**
     * The number of pills yet to be collected. Incremented  by Pill.onActivate
     */
    var pills: Int = 0

    override fun sceneBegin() {
        pills = 0
    }

    var lives: Int = 3

    override fun sceneActivated() {
        Game.instance.scene.findStage("glass")?.let { glass ->
            scoreRole = glass.actors.firstOrNull { it.role is Score }?.role as Score
            scoreRole?.update(score)
            livesIndicator = glass.actors.firstOrNull { it.role is Lives }?.role as Lives
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
            Play.instance.eatenPowerPill()
        } else {
            addPoints(1)
        }
        pills--
        if (pills <= 0) {
            Player.instance.levelComplete()

            Player.instance.actor.stage!!.findRole<Ghost>().forEach { ghost ->
                ghost.levelComplete()
            }
        }
    }

    fun eatenBonus(): Int {
        val points = bonusPoints
        addPoints(points)
        bonusPoints *= 2
        return points
    }

    /**
     * If the bonus timed out before being eaten then the bonus is reset
     */
    fun missedBonus() {
        bonusPoints = STARTING_BONUS
    }

    fun addPoints(points: Int) {
        score += points
        scoreRole?.update(score)
    }

    fun playerDied(): Boolean {
        lives--
        livesIndicator?.playerDied()
        Game.instance.scene.findStage("main")?.let { main ->
            main.actors.filter { it.role is Ghost }.forEach { actor ->
                (actor.role as Ghost).playerDied()
            }
        }
        return lives > 0
    }


    companion object {
        lateinit var instance: PillPopper
    }
}
