package org.overrun.ktgl.scene

/**
 * @author squid233
 * @since 0.1.0
 */
class Behavior : IBehavior {
    private var fixedUpdate: UpdateCallback = null
    private var update: UpdateCallback = null
    private var lateUpdate: UpdateCallback = null

    fun fixedUpdate(delta: Double) {
        fixedUpdate?.invoke(delta)
    }

    fun update(delta: Double) {
        update?.invoke(delta)
    }

    fun lateUpdate(delta: Double) {
        lateUpdate?.invoke(delta)
    }

    override fun onFixedUpdate(block: UpdateCallback) {
        fixedUpdate = block
    }

    override fun onUpdate(block: UpdateCallback) {
        update = block
    }

    override fun onLateUpdate(block: UpdateCallback) {
        lateUpdate = block
    }
}
