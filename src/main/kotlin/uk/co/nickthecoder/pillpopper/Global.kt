package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.neighbourhood.Block

val GRID_SIZE = 40

val TOUCHING_DISTANCE = 10.0

val STARTING_BONUS = 50

enum class Direction(val dx: Int, val dy: Int) {
    NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0);

    fun isOpposite(other: Direction): Boolean {
        return if (dx == 0) dy == -other.dy else dx == -other.dx
    }
}

fun Block.isSolid(dx: Int, dy: Int, ignoreDoors: Boolean = false): Boolean {

    val nextBlock = neighbouringBlock(dx, dy)

    return nextBlock?.isSolid(ignoreDoors) ?: true
}

fun Block.isSolid(ignoreDoors: Boolean = false): Boolean {
    occupants.forEach {
        if (it.role is Solid) {
            if (ignoreDoors) {
                if (it.role is Door) {
                    // Do nothing (ignore the door)
                } else {
                    return true
                }
            } else {
                return true
            }
        }
    }
    return false
}

fun Block.isDoor(): Boolean {
    occupants.forEach {
        if (it.role is Door) {
            return true
        }
    }
    return false
}


fun Block.isTunnel(): Boolean {
    occupants.forEach {
        if (it.role is Tunnel) {
            return true
        }
    }
    return false
}

