package uk.co.nickthecoder.pillpopper

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.stage.GameStage

/**
 * An example of how to optimise GameStage, so that findActorsAt does not have to iterate over every object.
 * In this game we don't use findActorsAt, so this
 */
class NeighbourhoodStage : GameStage() {

    override fun findActorsAt(point: Vector2d): List<Actor> {
        val block = Play.instance.neighbourhood.existingBlockAt(point.x, point.y)
        if (block == null) {
            return emptyList()
        }
        return block.occupants.map { it.actor }.filter { it.touching(point) }
    }

}
