package org.overrun.ktgl.gl

import org.joml.Vector4fc
import org.lwjgl.opengl.GL30C.*

/**
 * @author squid233
 * @since 0.1.0
 */
class GLStateMgr {
    private var clearR = 0f
    private var clearG = 0f
    private var clearB = 0f
    private var clearA = 0f
    var program = 0
        set(value) {
            if (field != value) {
                field = value
                glUseProgram(value)
            }
        }
    var vertexArray = 0
        set(value) {
            if (field != value) {
                field = value
                glBindVertexArray(value)
            }
        }

    fun useProgram(block: () -> Unit) {
        val prev = program
        block()
        program = prev
    }

    fun useProgram(pId: Int, block: (Int) -> Unit) {
        useProgram {
            program = pId
            block(pId)
        }
    }

    fun useVertexArray(va: Int, block: (Int) -> Unit) {
        val prev = vertexArray
        vertexArray = va
        block(va)
        vertexArray = prev
    }

    fun deleteProgram(program: Int) {
        glDeleteProgram(program)
        this.program = 0
    }

    fun deleteVertexArray(vertexArray: Int) {
        glDeleteVertexArrays(vertexArray)
        this.vertexArray = 0
    }

    fun setClearColor(clearColor: Vector4fc) {
        setClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w())
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        if (clearR != r ||
            clearG != g ||
            clearB != b ||
            clearA != a
        ) {
            clearR = r
            clearG = g
            clearB = b
            clearA = a
            glClearColor(r, g, b, a)
        }
    }
}
