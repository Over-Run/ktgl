package org.overrun.ktgl.scene

import org.joml.Vector4f
import org.lwjgl.opengl.GL11C.glClear
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit
import org.overrun.ktgl.util.math.ktglModelMat
import org.overrun.ktgl.util.math.ktglModelviewMat
import org.overrun.ktgl.util.math.ktglNormalMat
import org.overrun.ktgl.util.math.ktglProjMat
import org.overrun.ktgl.util.time.Time

/**
 * A ktgl scene.
 *
 * @param[id] the id of the scene
 * @author squid233
 * @since 0.1.0
 */
class Scene(val id: String) {
    var name: String = id
    val backgroundColor = Vector4f()
    var clearBit = GLClearBit.NONE
    private val gameObjects = GameObjects()
    private var customRender: (Scene.(Project) -> Unit)? = null

    class GameObjects {
        internal val objects = LinkedHashMap<String, GameObjectImpl>()

        fun add(name: String): GameObjectImpl {
            if (objects.containsKey(name)) {
                return objects[name]!!
            }
            return GameObjectImpl().also { objects[name] = it }
        }

        fun add(name: String, block: GameObjectImpl.() -> Unit): GameObjectImpl =
            add(name).apply(block)

        operator fun String.invoke(block: GameObjectImpl.() -> Unit): GameObjectImpl =
            add(this, block)
    }

    fun gameObjects(block: GameObjects.() -> Unit) = block(gameObjects)

    fun customRenderer(block: (Scene.(Project) -> Unit)?) {
        customRender = block
    }

    fun clearNow() = glClear(clearBit.bits)

    fun fixedUpdate(delta: Double) {
        gameObjects.objects.values.forEach { it.behavior?.fixedUpdate(it, delta) }
    }

    fun update(delta: Double) {
        gameObjects.objects.values.forEach { it.behavior?.update(it, delta) }
    }

    fun lateUpdate(delta: Double) {
        gameObjects.objects.values.forEach { it.behavior?.lateUpdate(it, delta) }
    }

    fun renderDefault(project: Project) {
        project.glStateMgr.setClearColor(backgroundColor)
        clearNow()

        project.glStateMgr.useProgram {
            gameObjects.objects.values.forEach {
                if (it.visible) {
                    val model = it.model.value
                    if (model != null) {
                        val shader = it.shader()
                        if (shader != null) {
                            val changed = shader.bind()
                            if (changed) {
                                shader.getDeltaTime()?.set(Time.deltaTime.toFloat())
                                shader.getCurrTime()?.set(Time.time.toFloat())
                            }
                            shader.getProjection()?.set(ktglProjMat)
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
    }

    fun render(project: Project) = customRender.let {
        if (it != null) it(project)
        else renderDefault(project)
    }
}
