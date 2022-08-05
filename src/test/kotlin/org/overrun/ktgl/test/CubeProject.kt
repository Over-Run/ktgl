package org.overrun.ktgl.test

import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit
import org.overrun.ktgl.gl.shader.BuiltinShaders
import org.overrun.ktgl.io.Input
import org.overrun.ktgl.model.Model
import org.overrun.ktgl.model.Vertex
import org.overrun.ktgl.scene.GameObject
import org.overrun.ktgl.util.hslToRgb
import org.overrun.ktgl.util.math.Direction
import org.overrun.ktgl.util.time.Time

const val MOUSE_SENSITIVITY = 0.15f

fun main() {
    Project("CubeProject") {
        val rot = Vector3f()

        "my_scene" {
            name = "My Scene"
            backgroundColor.set(0.4f, 0.8f, 1.0f, 1.0f)
            clearBit = GLClearBit.COLOR_DEPTH

            gameObjects {
                (GameObject("cube")) {
                    name = "Cube"
                    behavior {
                        onFixedUpdate {
                            hslToRgb(
                                Time.time.toFloat() * 0.32f / Time.fixedTimestep.toFloat() % 360f,
                                0.7f,
                                0.5f
                            ).also { (r, g, b) ->
                                color.set(r, g, b, 1f)
                            }
                        }
                        onUpdate { delta ->
                            rot.x += delta.toFloat()
                            rot.y -= delta.toFloat()
                            rotation.rotationY(rot.y).rotateX(rot.x)
                        }
                    }
                    shader { createBuiltinShader(BuiltinShaders.position) }
                    model {
                        val v0 = Vertex(-0.5f, 0.5f, -0.5f)
                        val v1 = Vertex(-0.5f, -0.5f, -0.5f)
                        val v2 = Vertex(-0.5f, -0.5f, 0.5f)
                        val v3 = Vertex(-0.5f, 0.5f, 0.5f)
                        val v4 = Vertex(0.5f, 0.5f, 0.5f)
                        val v5 = Vertex(0.5f, -0.5f, 0.5f)
                        val v6 = Vertex(0.5f, -0.5f, -0.5f)
                        val v7 = Vertex(0.5f, 0.5f, -0.5f)
                        Model {
                            // -x
                            face(v0, v1, v2, v3)
                            // +x
                            face(v4, v5, v6, v7)
                            // -y
                            face(v1, v2, v5, v6)
                            // +y
                            face(v0, v3, v4, v7)
                            // -z
                            face(v7, v6, v1, v0)
                            // +z
                            face(v3, v2, v5, v4)
                        }
                    }
                }
            }
            onLateUpdate { delta ->
                val speed = 2.5f * delta.toFloat()
                Input.apply {
                    mainCamera.also {
                        if (getKeyDown(GLFW_KEY_W)) it.moveRelative(Direction.NORTH, speed)
                        if (getKeyDown(GLFW_KEY_S)) it.moveRelative(Direction.SOUTH, speed)
                        if (getKeyDown(GLFW_KEY_A)) it.moveRelative(Direction.WEST, speed)
                        if (getKeyDown(GLFW_KEY_D)) it.moveRelative(Direction.EAST, speed)
                        if (getKeyDown(GLFW_KEY_LEFT_SHIFT) ||
                            getKeyDown(GLFW_KEY_RIGHT_SHIFT)
                        ) it.moveRelative(Direction.DOWN, speed)
                        if (getKeyDown(GLFW_KEY_SPACE)) it.moveRelative(Direction.UP, speed)
                    }
                }
            }
            onCursorPos { _, _, _, _, dtX, dtY ->
                if (Input.mouseGrabbed) {
                    mainCamera.rotate(
                        Math.toRadians(-dtX.toFloat() * MOUSE_SENSITIVITY),
                        Math.toRadians(-dtY.toFloat() * MOUSE_SENSITIVITY)
                    )
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
                    GLFW_KEY_ESCAPE -> Input.mouseGrabbed = !Input.mouseGrabbed
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
