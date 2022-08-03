package org.overrun.ktgl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C
import org.lwjgl.system.APIUtil
import org.overrun.ktgl.gl.GLStateMgr
import org.overrun.ktgl.gl.shader.GLShader
import org.overrun.ktgl.io.Window
import org.overrun.ktgl.scene.Scene
import org.overrun.ktgl.util.time.Time
import org.overrun.ktgl.util.time.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

var currentProject: Project? = null
var currentGLStateMgr: GLStateMgr? = null

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
    internal val scenes = HashMap<String, Scene>()
    var logger: Logger = LoggerFactory.getLogger(name)
    val hints = GLFWHints()
    val window = Window(title = name)
    var currScene: String? = null

    private val timer = Timer()

    private val shaders = HashMap<String, GLShader>()

    private var errorCallback: ((Int, String) -> Unit)? = null
    private var preStart = { }
    private var start = { }
    private var postStart = { }
    private var preRunning = { }
    private var running = { }
    private var postRunning = { }
    private var close = { }

    lateinit var glStateMgr: GLStateMgr
        private set

    constructor(name: String, block: Project.() -> Unit) : this(name) {
        block()
    }

    fun createBuiltinShader(shader: Lazy<GLShader>) = createShader(shader.value)
    fun createShader(id: String): GLShader = createShader(GLShader(id))

    fun createShader(shader: GLShader): GLShader {
        val id = shader.id
        if (shaders.containsKey(id)) {
            return shaders[id]!!
        }
        return shader.also { shaders[id] = it }
    }

    fun getShader(id: String): GLShader? = shaders[id]

    fun removeShader(id: String) {
        shaders[id]?.close()
        shaders.remove(id)
    }

    fun createScene(id: String): Scene {
        if (scenes.containsKey(id)) {
            return this[id]
        }
        return Scene(id).also { scenes[id] = it }
    }

    inline fun createScene(id: String, block: Scene.() -> Unit): Scene = createScene(id).apply(block)
    inline operator fun String.invoke(block: Scene.() -> Unit): Scene = createScene(this, block)

    fun Scene.render() = render(this@Project)

    fun renderScene(id: String) = this[id].render()

    operator fun get(id: String): Scene = scenes[id]!!
    inline operator fun get(id: String, block: Scene.() -> Unit): Scene = this[id].apply(block)

    fun hints(block: (GLFWHints) -> Unit) = block(hints)
    fun hints(vararg hintPairs: Pair<Int, Int>) = hintPairs.forEach { (hint, value) -> hints[hint] = value }

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

    fun preRunning(block: () -> Unit) {
        preRunning = block
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
        glfwSetFramebufferSizeCallback(window.handle) { _, width, height ->
            GL11C.glViewport(0, 0, width, height)
        }

        preStart()
        window.makeCtxCurr()
        GL.createCapabilities(true)
        glStateMgr = GLStateMgr()
        currentGLStateMgr = glStateMgr
        start()
        window.show()
        postStart()

        timer.advanceTime()

        while (!window.shouldClose()) {
            timer.advanceTime()
            preRunning()
            currScene?.also {
                this[it].apply {
                    for (i in 0 until timer.ticks) {
                        fixedUpdate(Time.fixedTimestep)
                    }
                    update(Time.deltaTime)
                    lateUpdate(Time.deltaTime)
                    render()
                }
            }
            running()
            window.swapBuffers()
            window.pollEvents()
            postRunning()
        }
    }

    fun runFinally() = use { run() }

    override fun close() {
        close.invoke()
        shaders.values.forEach(GLShader::close)
        window.close()
        glfwTerminate()
        glfwSetErrorCallback(null)?.close()
    }

    inline operator fun invoke(block: Project.() -> Unit) = block()
}
