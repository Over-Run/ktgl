package org.overrun.ktgl.io

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL

/**
 * A ktgl window.
 *
 * @param[width] the desired width, in screen coordinates, of the window
 * @param[height] the desired height, in screen coordinates, of the window
 * @param[title] initial, UTF-8 encoded window title
 * @param[monitor] the monitor to use for fullscreen mode, or `NULL` for windowed mode
 * @param[share] the window whose context to share resources with, or `NULL` to not share resources
 * @author squid233
 * @since 0.1.0
 */
class Window(
    width: Int = 800,
    height: Int = 600,
    title: CharSequence = "",
    monitor: Long = NULL,
    var share: Long = NULL
) : AutoCloseable {
    var width = width
        set(value) {
            field = value
            if (handle != NULL)
                glfwSetWindowSize(handle, field, height)
        }
    var height = height
        set(value) {
            field = value
            if (handle != NULL)
                glfwSetWindowSize(handle, width, field)
        }
    var title = title
        set(value) {
            field = value
            if (handle != NULL)
                glfwSetWindowTitle(handle, field)
        }
    var monitor = monitor
        private set
    var handle: Long = NULL
        private set

    var fbWidth = width
        internal set
    var fbHeight = height
        internal set

    internal var keyPress: KeyCallback = null
    internal var keyRelease: KeyCallback = null
    internal var keyRepeat: KeyCallback = null
    internal var cursorPos: ICursorPosCallback? = null

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
        glfwSetWindowSize(handle, width, height)
    }

    fun setMonitor(
        monitor: Long,
        xpos: Int, ypos: Int,
        width: Int, height: Int,
        refreshRate: Int
    ) {
        this.monitor = monitor
        glfwSetWindowMonitor(handle, monitor, xpos, ypos, width, height, refreshRate)
    }

    infix fun moveToCenter(monitor: Long) {
        glfwGetVideoMode(monitor)?.apply { moveToCenter(width(), height()) }
    }

    fun moveToCenter(vidModeWidth: Int, vidModeHeight: Int) {
        MemoryStack.stackPush().use {
            val pWidth = it.mallocInt(1)
            val pHeight = it.mallocInt(1)
            glfwGetWindowSize(handle, pWidth, pHeight)
            glfwSetWindowPos(
                handle,
                (vidModeWidth - pWidth[0]) / 2,
                (vidModeHeight - pHeight[0]) / 2
            )
        }
    }

    fun onKeyPress(block: KeyCallback) {
        keyPress = block
    }

    fun onKeyRelease(block: KeyCallback) {
        keyRelease = block
    }

    fun onKeyRepeat(block: KeyCallback) {
        keyRepeat = block
    }

    fun onCursorPos(block: ICursorPosCallback?) {
        cursorPos = block
    }

    fun makeCtxCurr() = glfwMakeContextCurrent(handle)

    /**
     * @see glfwSwapInterval
     */
    infix fun swapInterval(interval: Int) = glfwSwapInterval(interval)

    fun show() = glfwShowWindow(handle)
    fun hide() = glfwHideWindow(handle)

    fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    fun swapBuffers() = glfwSwapBuffers(handle)

    fun pollEvents() = glfwPollEvents()
    fun waitEvents() = glfwWaitEvents()

    infix fun setShouldClose(value: Boolean) = glfwSetWindowShouldClose(handle, value)

    fun withHandle(block: (handle: Long) -> Unit) {
        block(handle)
    }

    operator fun invoke() {
        handle = glfwCreateWindow(width, height, title, monitor, share)
        if (handle == NULL)
            throw RuntimeException("Failed to create the GLFW window")
    }

    override fun close() {
        Callbacks.glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
    }
}

private typealias KeyCallback = ((key: Int, scancode: Int, mods: Int) -> Unit)?
