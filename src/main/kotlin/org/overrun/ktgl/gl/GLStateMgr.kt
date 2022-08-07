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
    var depthTest = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    glEnable(GL_DEPTH_TEST)
                } else {
                    glDisable(GL_DEPTH_TEST)
                }
            }
        }
    var depthFunc: Int = GL_LESS
        set(value) {
            if (field != value) {
                field = value
                glDepthFunc(value)
            }
        }
    var cullFace = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    glEnable(GL_CULL_FACE)
                } else {
                    glDisable(GL_CULL_FACE)
                }
            }
        }
    var activeTexture = 0
        set(value) {
            if (field != value) {
                field = value
                glActiveTexture(GL_TEXTURE0 + value)
            }
        }
    val maxImageUnits = glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)
    private val texturesCubeMap = IntArray(maxImageUnits)

    fun setTextureCubeMap(unit: Int, id: Int) {
        activeTexture = unit
        setTextureCubeMap(id)
    }

    fun setTextureCubeMap(id: Int) {
        if (texturesCubeMap[activeTexture] != id) {
            texturesCubeMap[activeTexture] = id
            glBindTexture(GL_TEXTURE_CUBE_MAP, id)
        }
    }

    fun useProgram(block: () -> Unit) {
        val prev = program
        block()
        program = prev
    }

    fun useProgram(pId: Int, block: (Int) -> Unit) {
        val prev = program
        program = pId
        block(pId)
        program = prev
    }

    fun useVertexArray(va: Int, block: (Int) -> Unit) {
        val prev = vertexArray
        vertexArray = va
        block(va)
        vertexArray = prev
    }

    fun useTextureCubeMap(id: Int, block: (Int) -> Unit) {
        val prev = texturesCubeMap[activeTexture]
        setTextureCubeMap(id)
        block(id)
        setTextureCubeMap(prev)
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
