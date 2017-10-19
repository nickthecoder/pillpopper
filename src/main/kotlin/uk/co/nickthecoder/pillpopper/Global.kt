package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.neighbourhood.Block

val GRID_SIZE = 40

val TOUCHING_DISTANCE = 10.0

val STARTING_BONUS = 50

fun Block<Role>.neighbour(direction: Direction) = neighbouringBlock(direction.dx, direction.dy)
