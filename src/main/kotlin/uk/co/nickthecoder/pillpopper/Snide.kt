package uk.co.nickthecoder.pillpopper

private val AWAY = GRID_SIZE * 10

class Snide : Ghost() {

    /**
     * Aim for where the Player will be later (if he keeps straight)
     */
    override val chaseScorer = { dir: Direction ->
        scoreDirectlyTo(dir,
                Player.instance.actor.x + Player.instance.direction.dx * AWAY - actor.x,
                Player.instance.actor.y + Player.instance.direction.dy * AWAY - actor.y)
    }
}
