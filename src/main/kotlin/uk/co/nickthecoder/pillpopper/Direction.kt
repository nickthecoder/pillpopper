package uk.co.nickthecoder.pillpopper


enum class Direction(val dx: Int, val dy: Int) {
    NONE(0, 0), NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0);

    fun isOpposite(other: Direction): Boolean {
        return if (dx == 0) dy == -other.dy else dx == -other.dx
    }

    fun opposite() =
            when (this) {
                NONE -> NONE
                NORTH -> SOUTH
                SOUTH -> NORTH
                EAST -> WEST
                WEST -> EAST
            }
}
