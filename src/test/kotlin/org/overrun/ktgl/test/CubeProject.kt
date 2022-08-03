package org.overrun.ktgl.test

import org.lwjgl.glfw.GLFW.*
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit
import org.overrun.ktgl.gl.shader.BuiltinShaders
import org.overrun.ktgl.model.Model
import org.overrun.ktgl.model.Vertex
import org.overrun.ktgl.util.hslToRgb
import org.overrun.ktgl.util.time.Time

fun main() {
    Project("CubeProject") {
        "my_scene" {
            name = "My Scene"
            backgroundColor.set(0.4F, 0.8F, 1.0F, 1.0F)
            clearBit = GLClearBit.COLOR_DEPTH

            gameObjects {
                "Cube" {
                    behavior {
                        onFixedUpdate {
                            hslToRgb(
                                Time.time.toFloat() / Time.fixedTimestep.toFloat() * 0.5F % 360F,
                                0.7F,
                                0.5F
                            ).also { (r, g, b) ->
                                color.set(r, g, b, 1F)
                            }
                        }
                        onUpdate { delta ->
                            rotation.rotateZ(delta.toFloat())
                        }
                    }
                    shader { createBuiltinShader(BuiltinShaders.position) }
                    model {
                        Model {
                            face(
                                Vertex(-0.5F, 0.5F),
                                Vertex(-0.5F, -0.5F),
                                Vertex(0.5F, -0.5F),
                                Vertex(0.5F, 0.5F)
                            )
                        }
                    }
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
        }
        preRunning {
            currScene = "my_scene"
        }
    }.runFinally()
}
