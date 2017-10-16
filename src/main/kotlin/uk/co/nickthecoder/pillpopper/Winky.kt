package uk.co.nickthecoder.pillpopper

class Winky : Ghost() {

    /**
     * Head directly for the player
     */
    override val chaseScorer = { dir: Direction ->
        scoreDirectlyTo(dir, Player.instance.actor.x, Player.instance.actor.y)
    }

}
