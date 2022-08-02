package org.overrun.ktgl.scene

import org.joml.Quaternionf
import org.joml.Vector3f
import org.overrun.ktgl.gl.GLDrawMode
import org.overrun.ktgl.gl.shader.GLShader
import org.overrun.ktgl.model.IModel

/**
 * ## Game Object
 *
 * @author squid233
 * @since 0.1.0
 */
abstract class GameObject<T : GameObject<T>> {
    var behavior: Behavior<T>? = null
    var visible = true
    var model: Lazy<IModel?> = lazyOf(null)
    var shader: () -> GLShader? = { null }
    var drawType = GLDrawMode.TRIANGLES

    /**
     * The rotation relative center
     */
    val anchor = Vector3f()

    val position = Vector3f()

    val rotation = Quaternionf()

    val scale = Vector3f()

    abstract fun getThis(): T

    fun behavior(block: Behavior<T>.() -> Unit) {
        val behavior = Behavior<T>()
        behavior.block()
        this.behavior = behavior
    }

    fun model(block: () -> IModel?) {
        // TODO: Thread safe
        model = lazy(LazyThreadSafetyMode.NONE, block)
    }

    fun shader(block: () -> GLShader?) {
        shader = block
    }

    fun Behavior<T>.update(delta: Double) {
        update(getThis(), delta)
    }

    fun Behavior<T>.fixedUpdate(delta: Double) {
        fixedUpdate(getThis(), delta)
    }
}

class GameObjectImpl : GameObject<GameObjectImpl>() {
    override fun getThis(): GameObjectImpl = this
}
