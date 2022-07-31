package org.overrun.ktgl.test

import org.lwjgl.glfw.GLFW.*
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit

fun main() {
    Project("CubeProject") {
        "MyScene" {
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
        }
        hints(
            GLFW_VISIBLE to GLFW_FALSE,
            GLFW_RESIZABLE to GLFW_FALSE
        )
        window {
            title = "3D Cube"
            onKeyPress { key, _, _ ->
                when (key) {
                    GLFW_KEY_ESCAPE -> window.setShouldClose(true)
                }
            }
        }
        beforeStart {
            window.moveToCenter(glfwGetPrimaryMonitor())
        }
        onStart {
            window swapInterval 1
        }
        onRunning {
            currScene = "MyScene"
        }
    }.runFinally()
}
