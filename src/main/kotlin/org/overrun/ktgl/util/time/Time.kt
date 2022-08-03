package org.overrun.ktgl.util.time

import org.lwjgl.glfw.GLFW

object Time {
    @JvmField
    var fixedTimestep = 0.02

    @JvmField
    var maxTimestep = 0.3333333

    @JvmField
    var timeScale = 1.0

    @JvmField
    var deltaTime = 0.0

    /**
     * Get the current timer value.
     *
     * @return the current value, in seconds, or zero if an error occurred
     */
    @JvmStatic
    val time: Double
        get() = GLFW.glfwGetTime()
}

class Timer {
    private var lastTime = 0.0
    private var passedTime = 0.0
    var partialTick = 0.0
        private set
    var ticks = 0
        private set

    fun advanceTime() {
        val realTime = Time.time
        Time.deltaTime = realTime - lastTime
        lastTime = realTime
        val invTs = 1.0 / Time.fixedTimestep
        passedTime += Time.deltaTime * Time.timeScale * invTs
        ticks = passedTime.toInt()
        if (ticks < 0) ticks = 0
        if (ticks > Time.maxTimestep * invTs) ticks = (Time.maxTimestep * invTs).toInt()
        passedTime -= ticks
        partialTick = passedTime
    }
}
