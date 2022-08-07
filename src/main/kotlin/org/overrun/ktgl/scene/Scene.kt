package org.overrun.ktgl.scene

import org.joml.Vector4f
import org.lwjgl.opengl.GL11C
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit
import org.overrun.ktgl.io.ICursorPosCallback
import org.overrun.ktgl.util.math.*
import org.overrun.ktgl.util.time.Time

/**
 * A ktgl scene.
 *
 * @param[id] the id of the scene
 * @author squid233
 * @since 0.1.0
 */
class Scene @JvmOverloads constructor(
    val id: String,
    private val behavior: Behavior = Behavior()
) : IBehavior by behavior, AutoCloseable {
    var name: String = id
    val backgroundColor = Vector4f()
    var clearBit = GLClearBit.COLOR_DEPTH
    private val gameObjects = GameObjects<GameObject>()
    private val cameras = GameObjects<BaseCamera>()
    private var customRender: (Scene.(Project) -> Unit)? = null
    val mainCamera = cameras.add(FreeCamera("main_camera"))
    var currCamera: BaseCamera? = mainCamera
    internal var cursorPos: ICursorPosCallback? = null

    class GameObjects<T : GameObject> {
        internal val objects = LinkedHashMap<String, T>()

        fun <U : T> add(u: U): U =
            u.also { objects[it.id] = it }

        operator fun <U : T> U.invoke(block: U.() -> Unit): U =
            add(this).also(block)
    }

    fun gameObjects(block: GameObjects<GameObject>.() -> Unit) = block(gameObjects)

    fun cameras(block: GameObjects<BaseCamera>.() -> Unit) = block(cameras)

    fun customRenderer(block: (Scene.(Project) -> Unit)?) {
        customRender = block
    }

    @JvmOverloads
    fun clearNow(bits: GLClearBit = clearBit) = GL11C.glClear(bits.bits)

    fun fixedUpdate(delta: Double) {
        behavior.fixedUpdate(delta)
        gameObjects.objects.values.forEach { it.behavior?.fixedUpdate(delta) }
    }

    fun update(delta: Double) {
        behavior.update(delta)
        gameObjects.objects.values.forEach { it.behavior?.update(delta) }
    }

    fun lateUpdate(delta: Double) {
        behavior.lateUpdate(delta)
        gameObjects.objects.values.forEach { it.behavior?.lateUpdate(delta) }
    }

    fun onCursorPos(block: ICursorPosCallback?) {
        cursorPos = block
    }

    fun renderDefault(project: Project) {
        project.glStateMgr.depthTest = true
        project.glStateMgr.depthFunc = GL11C.GL_LEQUAL
        project.glStateMgr.cullFace = true
        currCamera.also {
            project.glStateMgr.setClearColor(
                it?.backgroundColor ?: backgroundColor
            )
            if (it != null) {
                when (it.clearFlags) {
                    ClearFlags.SKYBOX, ClearFlags.SOLID_COLOR -> clearNow(GLClearBit.COLOR_DEPTH)
                    ClearFlags.DEPTH_ONLY -> clearNow(GLClearBit.DEPTH)
                    ClearFlags.NONE -> {}
                }
            } else {
                clearNow()
            }
        }

        currCamera?.apply {
            if (orthographic) {
                // TODO: test
                ktglProjMat.setOrtho(viewportX, viewportW, viewportY, viewportH, zNear, zFar)
            } else {
                ktglProjMat.setPerspective(
                    fov,
                    aspect.let {
                        if (it == 0f) project.window.fbWidth.toFloat() / project.window.fbHeight.toFloat()
                        else aspect
                    },
                    zNear,
                    zFar
                )
            }
            getMatrix(ktglViewMat.identity())
            project.shaders.values.forEach {
                it.getProjection()?.set(ktglProjMat)
                it.getDeltaTime()?.set(Time.deltaTime.toFloat())
            }
        }

        // Render opaque objects
        project.glStateMgr.useProgram {
            gameObjects.objects.values.forEach {
                if (it.visible) {
                    val model = it.model.value
                    if (model != null) {
                        val shader = it.shader()
                        if (shader != null) {
                            shader.bind()
                            shader.getCurrTime()?.set(Time.time.toFloat())
                            ktglModelMat.apply {
                                translation(it.position)
                                    .translate(it.anchor)
                                    .rotate(it.rotation)
                                    .translate(-it.anchor.x, -it.anchor.y, -it.anchor.z)
                                    .scale(it.scale)
                                shader.getModelview()?.set(ktglModelviewMat)
                                shader.getNormal()?.set(ktglNormalMat)
                            }
                            shader.getColorModulator()?.set(it.color)
                            shader.uploadUniforms()
                            model.render(it.drawType)
                        }
                    }
                }
            }
        }

        // Render skybox
        currCamera?.apply {
            if (clearFlags == ClearFlags.SKYBOX) {
                skybox?.render(project, this@Scene)
            }
        }

        // Render transparency objects
    }

    fun render(project: Project) = customRender.let {
        if (it != null) it(project)
        else renderDefault(project)
    }

    override fun close() {
        currCamera?.skybox?.close()
    }
}
