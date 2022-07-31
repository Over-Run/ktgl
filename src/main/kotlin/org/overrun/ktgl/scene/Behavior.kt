package org.overrun.ktgl.scene

/**
 * @author squid233
 * @since 0.1.0
 */
class Behavior<T : GameObject<T>> : IBehavior<T> {
    private var update: UpdateCallback<T> = null
    private var fixedUpdate: UpdateCallback<T> = null

    fun update(gameObject: T, delta: Double) {
        update?.invoke(gameObject, delta)
    }

    fun fixedUpdate(gameObject: T, delta: Double) {
        fixedUpdate?.invoke(gameObject, delta)
    }

    override fun onUpdate(block: UpdateCallback<T>) {
        update = block
    }

    override fun onFixedUpdate(block: UpdateCallback<T>) {
        fixedUpdate = block
    }
}
