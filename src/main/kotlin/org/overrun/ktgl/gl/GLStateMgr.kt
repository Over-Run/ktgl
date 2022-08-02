package org.overrun.ktgl.gl

import org.joml.Vector4fc
import org.lwjgl.opengl.GL30C.*

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
