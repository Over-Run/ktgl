package org.overrun.ktgl.model

import org.overrun.ktgl.gl.GLDrawMode

/**
 * @author squid233
 * @since 0.1.0
 */
interface IModel {
    val meshes: List<IMesh>

    fun render(mode: GLDrawMode)
}
