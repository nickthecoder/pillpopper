package uk.co.nickthecoder.pillpopper

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.neighbourhood.Block

val GRID_SIZE = 40 // World coordinates

val TOUCHING_DISTANCE = 10.0 // World coordinates

val PILL_POINTS = 1

val POWER_PILL_POINTS = 10

val GHOST_POINTS = 50

val STARTING_BONUS = 100 // Points for eating a bonus in-sequence.

val WRONG_BONUS_POINTS = 10 // Points for eating a bonus out-of-sequence

val POWER_PILL_WARNING_TIME = 3.0 // Seconds

val INFO_HEIGHT = 40 // The height of the area with the scores etc.

val RESTART_PERIOD = 4.0 // seconds. Time that the Player takes to resurrect.

val BONUSES = 4 // Number of bonuses

fun Block<Role>.neighbour(direction: Direction) = neighbouringBlock(direction.dx, direction.dy)

