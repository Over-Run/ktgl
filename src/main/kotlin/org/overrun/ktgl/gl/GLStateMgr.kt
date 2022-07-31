package org.overrun.ktgl.gl

import org.joml.Vector4fc
import org.lwjgl.opengl.GL20C.glClearColor
import org.lwjgl.opengl.GL20C.glUseProgram

/**
 * @author squid233
 * @since 0.1.0
 */
class GLStateMgr {
    private var clearR = 0F
    private var clearG = 0F
    private var clearB = 0F
    private var clearA = 0F
    var program = 0
        set(value) {
            if (field != value) {
                field = value
                glUseProgram(value)
            }
        }

    fun setClearColor(clearColor: Vector4fc) {
        setClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w())
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        if (clearR.compareTo(r) != 0 ||
            clearG.compareTo(g) != 0 ||
            clearB.compareTo(b) != 0 ||
            clearA.compareTo(a) != 0
        ) {
            clearR = r
            clearG = g
            clearB = b
            clearA = a
            glClearColor(r, g, b, a)
        }
    }
}
