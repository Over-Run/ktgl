package org.overrun.ktgl.scene

import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * ## Game Object
 *
 * @author squid233
 * @since 0.1.0
 */
abstract class GameObject<T : GameObject<T>> {
    var behavior: Behavior<T>? = null

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
        block(behavior)
        this.behavior = behavior
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
