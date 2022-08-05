package org.overrun.ktgl.scene

/**
 * @author squid233
 * @since 0.1.0
 */
interface IBehavior {
    fun onFixedUpdate(block: UpdateCallback)

    fun onUpdate(block: UpdateCallback)

    fun onLateUpdate(block: UpdateCallback)
}

typealias UpdateCallback = ((delta: Double) -> Unit)?
