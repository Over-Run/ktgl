package org.overrun.ktgl.scene

import org.joml.Vector4f
import org.lwjgl.opengl.GL11C.glClear
import org.overrun.ktgl.Project
import org.overrun.ktgl.gl.GLClearBit

/**
 * A ktgl scene.
 *
 * @param[name] the name of the scene
 * @author squid233
 * @since 0.1.0
 */
class Scene(name: String) {
    var name = name
        private set
    val backgroundColor = Vector4f()
    var clearBit = GLClearBit.NONE
    private val gameObjects = GameObjects()

    class GameObjects {
        private val objects = LinkedHashMap<String, GameObjectImpl>()

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

    fun rename(project: Project, name: String) {
        project.scenes.remove(this.name)
        project.scenes[name] = this
    }

    fun gameObjects(block: GameObjects.() -> Unit) {
        block(gameObjects)
    }

    fun clearNow() {
        glClear(clearBit.bits)
    }

    fun render(project: Project) {
        project.glStateMgr.setClearColor(backgroundColor)
        clearNow()
    }
}
