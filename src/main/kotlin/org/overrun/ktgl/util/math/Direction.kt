package org.overrun.ktgl.util.math

import org.lwjgl.opengl.GL13C

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

    /**
     * North points to -z in normal or +z for skybox.
     */
    NORTH(0f, 0f, -1f, 4, 5),

    /**
     * South points to +z in normal or -z for skybox.
     */
    SOUTH(0f, 0f, 1f, 5, 4);

    val opposite: Direction
        get() = values()[oppositeId]
    val cubeMapId: Int = GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X + oppositeId

    override fun toString(): String = name.lowercase()
}
