package org.overrun.ktgl.gl

import org.lwjgl.opengl.GL40C.*

/**
 * @author squid233
 * @since 0.1.0
 */
enum class GLDrawMode(val glConst: Int) {
    POINTS(GL_POINTS),
    LINES(GL_LINES),
    LINE_LOOP(GL_LINE_LOOP),
    LINE_STRIP(GL_LINE_STRIP),
    TRIANGLES(GL_TRIANGLES),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN),
    LINES_ADJACENCY(GL_LINES_ADJACENCY),
    LINE_STRIP_ADJACENCY(GL_LINE_STRIP_ADJACENCY),
    TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY),
    TRIANGLE_STRIP_ADJACENCY(GL_TRIANGLE_STRIP_ADJACENCY),
    PATCHES(GL_PATCHES)
}
