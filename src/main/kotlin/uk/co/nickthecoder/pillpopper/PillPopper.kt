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

    var bonusIndicator: BonusIndicator? = null

    var score: Int = 0

    /**
     * Points for eating a scared ghost. Increases after each meal. Reset to 50 when eating a power pill.
     */
    var ghostPoints = 0

    /**
     * How many bonuses have been collected (in the correct sequence).
     */
    var bonusIndex = 0

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
            bonusIndicator = glass.actors.firstOrNull { it.role is BonusIndicator }?.role as BonusIndicator
            bonusIndicator?.initialise(bonusIndex)
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
            ghostPoints = GHOST_POINTS
            addPoints(POWER_PILL_POINTS)
            Play.instance.eatenPowerPill()
        } else {
            addPoints(PILL_POINTS)
        }
        pills--
        if (pills <= 0) {
            Player.instance.levelComplete()

            Player.instance.actor.stage!!.findRole<Ghost>().forEach { ghost ->
                ghost.levelComplete()
            }
        }
    }

    fun eatenBonus(index: Int): Int {
        if (index != bonusIndex) {
            addPoints(WRONG_BONUS_POINTS)
            return 0
        }

        val points = STARTING_BONUS.shl(index)
        addPoints(points)

        bonusIndicator?.bonusSequence(bonusIndex)
        if (index >= BONUSES - 1) {
            bonusIndex = 0
        } else {
            bonusIndex++
        }
        return points
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
