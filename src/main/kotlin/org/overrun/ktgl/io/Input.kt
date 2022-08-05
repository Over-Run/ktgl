package org.overrun.ktgl.io

import org.lwjgl.glfw.GLFW.*
import org.overrun.ktgl.currentProject
import kotlin.math.floor

/**
 * @author squid233
 * @since 0.1.0
 */
object Input {
    var mouseX = 0.0
        internal set
    var mouseY = 0.0
        internal set
    var dtMouseX = 0.0
        internal set
    var dtMouseY = 0.0
        internal set

    var mouseGrabbed = false
        set(value) {
            field = value
            currentProject?.also {
                glfwSetInputMode(it.window.handle, GLFW_CURSOR, if (value) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL)
            }
        }

    val intMouseX: Int
        get() = floor(mouseX).toInt()
    val intMouseY: Int
        get() = floor(mouseY).toInt()
    val intDtMouseX: Int
        get() = floor(dtMouseX).toInt()
    val intDtMouseY: Int
        get() = floor(dtMouseY).toInt()

    fun getMouseDown(button: Int): Boolean = currentProject.let {
        if (it == null) false
        else glfwGetMouseButton(it.window.handle, button) == GLFW_PRESS
    }

    fun getMouseUp(button: Int): Boolean = currentProject.let {
        if (it == null) false
        else glfwGetMouseButton(it.window.handle, button) == GLFW_RELEASE
    }

    fun getKeyDown(key: Int): Boolean = currentProject.let {
        if (it == null) false
        else glfwGetKey(it.window.handle, key) == GLFW_PRESS
    }

    fun getKeyUp(key: Int): Boolean = currentProject.let {
        if (it == null) true
        else glfwGetKey(it.window.handle, key) == GLFW_RELEASE
    }
}
