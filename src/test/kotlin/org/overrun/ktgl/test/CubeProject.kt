package org.overrun.ktgl.test

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit
import org.overrun.ktgl.gl.shader.ATTRIB_POSITION_LOC
import org.overrun.ktgl.gl.shader.BuiltinShaders
import org.overrun.ktgl.gl.shader.useShader
import org.overrun.ktgl.io.put
import org.overrun.ktgl.io.use

fun main() {
    var vao = 0
    var vbo: Int
    Project("CubeProject") {
        "my_scene" {
            name = "My Scene"
            backgroundColor.set(0.4F, 0.8F, 1.0F, 1.0F)
            clearBit = GLClearBit.COLOR_DEPTH

            gameObjects {
                "Cube" {
                    behavior {
                        onFixedUpdate { delta ->
                        }
                    }
                }
            }

            customRenderer {
                renderDefault(it)
                useShader(BuiltinShaders.position.value) {
                    uploadUniforms()
                    glStateMgr.vertexArray = vao
                    glDrawArrays(GL_TRIANGLES, 0, 3)
                    glStateMgr.vertexArray = 0
                }
            }
        }
        hints(
            GLFW_VISIBLE to GLFW_FALSE,
            GLFW_RESIZABLE to GLFW_FALSE
        )
        window {
            title = "3D Cube"
            onKeyPress { key, _, _ ->
                when (key) {
                    GLFW_KEY_ESCAPE -> window setShouldClose true
                }
            }
        }
        beforeStart {
            window moveToCenter glfwGetPrimaryMonitor()
        }
        onStart {
            window swapInterval 1
            createShader(BuiltinShaders.position.value)
            vao = glGenVertexArrays()
            glStateMgr.vertexArray = vao
            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            MemoryUtil.memAllocFloat(9).put(
                0.0F, 0.5F, 0.0F,
                -0.5F, -0.5F, 0.0F,
                0.5F, -0.5F, 0.0F
            ).flip().use {
                glBufferData(GL_ARRAY_BUFFER, it, GL_STATIC_DRAW)
            }
            glEnableVertexAttribArray(ATTRIB_POSITION_LOC)
            glVertexAttribPointer(ATTRIB_POSITION_LOC, 3, GL_FLOAT, false, 0, 0L)
            glStateMgr.vertexArray = 0
        }
        onRunning {
            currScene = "my_scene"
        }
    }.runFinally()
}
