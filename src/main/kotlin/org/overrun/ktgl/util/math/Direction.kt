package org.overrun.ktgl.util.math

/**
 * @author squid233
 * @since 0.1.0
 */
enum class Direction(
    val x: Float,
    val y: Float,
    val z: Float,
    val id: Int,
    val oppositeId: Int
) {
    WEST(-1f, 0f, 0f, 0, 1),
    EAST(1f, 0f, 0f, 1, 0),
    DOWN(0f, -1f, 0f, 2, 3),
    UP(0f, 1f, 0f, 3, 2),
    NORTH(0f, 0f, -1f, 4, 5),
    SOUTH(0f, 0f, 1f, 5, 4);

    val opposite: Direction
        get() = values()[oppositeId]
}
