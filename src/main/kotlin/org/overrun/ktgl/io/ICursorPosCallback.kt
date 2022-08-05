package org.overrun.ktgl.io

/**
 * The cursor pos callback.
 *
 * @author squid233
 * @since 0.1.0
 */
fun interface ICursorPosCallback {
    fun onCursorPos(
        lastX: Double, lastY: Double,
        x: Double, y: Double,
        dtX: Double, dtY: Double
    )
}
