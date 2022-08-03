package org.overrun.ktgl.scene

/**
 * @author squid233
 * @since 0.1.0
 */
class Behavior<T : GameObject<T>> : IBehavior<T> {
    private var fixedUpdate: UpdateCallback<T> = null
    private var update: UpdateCallback<T> = null
    private var lateUpdate: UpdateCallback<T> = null

    fun fixedUpdate(gameObject: T, delta: Double) {
        fixedUpdate?.invoke(gameObject, delta)
    }

    fun update(gameObject: T, delta: Double) {
        update?.invoke(gameObject, delta)
    }

    fun lateUpdate(gameObject: T, delta: Double) {
        lateUpdate?.invoke(gameObject, delta)
    }

    override fun onFixedUpdate(block: UpdateCallback<T>) {
        fixedUpdate = block
    }

    override fun onUpdate(block: UpdateCallback<T>) {
        update = block
    }

    override fun onLateUpdate(block: UpdateCallback<T>) {
        lateUpdate = block
    }
}
