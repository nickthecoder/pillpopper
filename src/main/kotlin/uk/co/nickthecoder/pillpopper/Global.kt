package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.neighbourhood.Block

val GRID_SIZE = 40 // World coordinates

val TOUCHING_DISTANCE = 10.0 // World coordinates

val STARTING_BONUS = 50 // Points

val POWER_PILL_WARNING_TIME = 3.0 // Seconds

val INFO_HEIGHT = 60 // The height of the area with the scores etc.

fun Block<Role>.neighbour(direction: Direction) = neighbouringBlock(direction.dx, direction.dy)
