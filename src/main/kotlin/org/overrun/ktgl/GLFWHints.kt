package org.overrun.ktgl

import org.lwjgl.glfw.GLFW.*

/**
 * @author squid233
 * @since 0.1.0
 */
class GLFWHints {
    private val map = HashMap<Int, Int>()

    operator fun set(hint: Int, value: Int) {
        map[hint] = value
    }

    operator fun set(hint: Int, value: Boolean) {
        map[hint] = if (value) GLFW_TRUE else GLFW_FALSE
    }

    fun setAll() {
        map.forEach { (hint, value) -> glfwWindowHint(hint, value) }
    }

    fun clear() = map.clear()
}
