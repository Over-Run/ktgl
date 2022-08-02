package org.overrun.ktgl.scene

/**
 * @author squid233
 * @since 0.1.0
 */
interface IBehavior<T : GameObject<T>> {
    fun onUpdate(block: UpdateCallback<T>)

    fun onFixedUpdate(block: UpdateCallback<T>)
}

typealias UpdateCallback<T> = (T.(delta: Double) -> Unit)?
