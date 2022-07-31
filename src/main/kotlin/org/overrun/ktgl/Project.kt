package org.overrun.ktgl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.APIUtil
import org.overrun.ktgl.gl.GLStateMgr
import org.overrun.ktgl.io.Window
import org.overrun.ktgl.scene.Scene
import org.slf4j.Logger
import org.slf4j.LoggerFactory

var currentProject: Project? = null

/**
 * A ktgl project.
 *
 * @param[name] The name of the project.
 * @author squid233
 * @since 0.1.0
 */
class Project(name: String) : Runnable, AutoCloseable {
    var name = name
        set(value) {
            field = value
            logger = LoggerFactory.getLogger(name)
        }
    internal val scenes = LinkedHashMap<String, Scene>()
    var logger: Logger = LoggerFactory.getLogger(name)
    val hints = GLFWHints()
    val window = Window(title = name)
    var currScene: String? = null
    private var errorCallback: ((Int, String) -> Unit)? = null
    private var preStart = { }
    private var start = { }
    private var postStart = { }
    private var running = { }
    private var postRunning = { }
    private var close = { }
    lateinit var glStateMgr: GLStateMgr
        private set

    constructor(name: String, block: Project.() -> Unit) : this(name) {
        block(this)
    }

    fun createScene(name: String): Scene {
        if (scenes.containsKey(name)) {
            return this[name]
        }
        return Scene(name).also { scenes[name] = it }
    }

    inline fun createScene(name: String, block: Scene.() -> Unit): Scene = createScene(name).apply(block)
    inline operator fun String.invoke(block: Scene.() -> Unit): Scene = createScene(this, block)

    fun Scene.render() {
        render(this@Project)
    }

    fun Scene.rename(name: String) {
        rename(this@Project, name)
    }

    fun renderScene(name: String) {
        this[name].render()
    }

    fun renameScene(name: String, newName: String) {
        this[name].rename(newName)
    }

    operator fun get(name: String): Scene = scenes[name]!!
    inline operator fun get(name: String, block: Scene.() -> Unit): Scene {
        val scene = this[name]
        block(scene)
        return scene
    }

    fun hints(block: (GLFWHints) -> Unit) = block(hints)
    fun hints(vararg hintPairs: Pair<Int, Int>) {
        hintPairs.forEach { (hint, value) -> hints[hint] = value }
    }

    inline fun window(block: Window.() -> Unit) = block(window)

    fun onError(block: ((error: Int, description: String) -> Unit)?) {
        errorCallback = block
    }

    fun beforeStart(block: () -> Unit) {
        preStart = block
    }

    fun onStart(block: () -> Unit) {
        start = block
    }

    fun afterStart(block: () -> Unit) {
        postStart = block
    }

    fun onRunning(block: () -> Unit) {
        running = block
    }

    fun postRunning(block: () -> Unit) {
        postRunning = block
    }

    fun onClose(block: () -> Unit) {
        close = block
    }

    /**
     * Run this project.
     */
    override fun run() {
        currentProject = this
        (if (errorCallback != null) GLFWErrorCallback.create { error, description ->
            errorCallback?.invoke(
                error,
                GLFWErrorCallback.getDescription(description)
            )
        } else GLFWErrorCallback.create(object : GLFWErrorCallback() {
            // Created from LWJGL GLFWErrorCallback

            private val ERROR_CODES = APIUtil.apiClassTokens(
                { _, value -> value in 0x10001..0x1ffff },
                null,
                org.lwjgl.glfw.GLFW::class.java
            )

            override fun invoke(error: Int, description: Long) {
                val msg = getDescription(description)

                logger.error("[LWJGL] {} error", ERROR_CODES[error])
                logger.error("\tDescription : {}", msg)
                logger.error("\tStacktrace  :")
                val stack = Thread.currentThread().stackTrace
                for (i in 4 until stack.size) {
                    logger.error("\t\t{}", stack[i].toString())
                }
            }
        })).set()

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        hints.setAll()
        hints.clear()

        window()
        glfwSetKeyCallback(window.handle) { _, key, scancode, action, mods ->
            when (action) {
                GLFW_PRESS -> window.keyPress
                GLFW_RELEASE -> window.keyRelease
                GLFW_REPEAT -> window.keyRepeat
                else -> null
            }?.invoke(key, scancode, mods)
        }

        preStart()
        window.makeCtxCurr()
        GL.createCapabilities(true)
        glStateMgr = GLStateMgr()
        start()
        window.show()
        postStart()

        while (!window.shouldClose()) {
            running()
            currScene?.also { this[it].render() }
            window.swapBuffer()
            window.pollEvents()
            postRunning()
        }
    }

    fun runFinally() = use { run() }

    override fun close() {
        close.invoke()
        window.close()
        glfwTerminate()
        glfwSetErrorCallback(null)?.close()
    }

    inline operator fun invoke(block: Project.() -> Unit) {
        block(this)
    }
}
