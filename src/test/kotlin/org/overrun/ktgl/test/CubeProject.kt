package org.overrun.ktgl.test

import org.joml.Math
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL12C.GL_NEAREST
import org.overrun.ktgl.Project
import org.overrun.ktgl.asset.TextureCubeMap
import org.overrun.ktgl.asset.TextureParam
import org.overrun.ktgl.io.IFileProvider
import org.overrun.ktgl.io.Input
import org.overrun.ktgl.model.POSITION
import org.overrun.ktgl.model.obj.ObjReader
import org.overrun.ktgl.scene.ClearFlags
import org.overrun.ktgl.scene.GameObject
import org.overrun.ktgl.scene.SkyboxCubeMap
import org.overrun.ktgl.util.ResourceType
import org.overrun.ktgl.util.hsvToRgb
import org.overrun.ktgl.util.math.Direction
import org.overrun.ktgl.util.time.Time
import org.overrun.ktgl.util.toFile

const val MOUSE_SENSITIVITY = 0.15f

fun main() {
    Project("CubeProject") {
        val rot = Vector3f()

        "my_scene" {
            name = "My Scene"
            backgroundColor.set(0.4f, 0.8f, 1.0f, 1.0f)
            mainCamera.clearFlags = ClearFlags.SKYBOX
            mainCamera.skybox = load(SkyboxCubeMap()).apply {
                texture = lazy(LazyThreadSafetyMode.NONE) {
                    load(
                        TextureCubeMap(
                            mapOf(
                                Direction.EAST to ("ktgl-test:textures/skybox/px.png" toFile ResourceType.ASSETS),
                                Direction.WEST to ("ktgl-test:textures/skybox/nx.png" toFile ResourceType.ASSETS),
                                Direction.UP to ("ktgl-test:textures/skybox/py.png" toFile ResourceType.ASSETS),
                                Direction.DOWN to ("ktgl-test:textures/skybox/ny.png" toFile ResourceType.ASSETS),
                                Direction.SOUTH to ("ktgl-test:textures/skybox/nz.png" toFile ResourceType.ASSETS),
                                Direction.NORTH to ("ktgl-test:textures/skybox/pz.png" toFile ResourceType.ASSETS)
                            ),
                            TextureParam(
                                minFilter = GL_NEAREST,
                                magFilter = GL_NEAREST,
                                wrapS = GL_CLAMP_TO_EDGE,
                                wrapT = GL_CLAMP_TO_EDGE,
                                wrapR = GL_CLAMP_TO_EDGE
                            ),
                            IFileProvider.SYSTEM
                        )
                    )
                }
            }

            gameObjects {
                (GameObject("cube")) {
                    name = "Cube"
                    position.set(-0.5f)
                    anchor.set(0.5f)
                    behavior {
                        onFixedUpdate {
                            hsvToRgb(
                                Time.time.toFloat() * 0.32f / Time.fixedTimestep.toFloat() % 360f,
                                0.6f,
                                0.8f
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
                    shader { createBuiltinShader { position } }
                    model {
                        ObjReader().load(
                            "ktgl-test:models/cube.obj" toFile ResourceType.ASSETS,
                            POSITION,
                            IFileProvider.SYSTEM
                        )
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
