package org.overrun.ktgl.scene

import org.joml.Math.cosFromSin
import org.joml.Math.sin
import org.joml.Matrix4f
import org.overrun.ktgl.util.math.Direction
import org.overrun.ktgl.util.math.RAD360F
import org.overrun.ktgl.util.math.RAD60F
import org.overrun.ktgl.util.math.RAD90F

/**
 * @author squid233
 * @since 0.1.0
 */
interface ICamera {
    fun getMatrix(mat: Matrix4f): Matrix4f
}

/**
 * @author squid233
 * @since 0.1.0
 */
abstract class BaseCamera(id: String) : ICamera, GameObject(id) {
    var orthographic = false
    var zNear = 0.3f
    var zFar = 1000f
    var fov = RAD60F

    /**
     * The aspect ratio. Set to `0F` to use framebuffer width and height ratio.
     */
    var aspect = 0f
    var size = 6f
    var viewportX = 0f
    var viewportY = 0f
    var viewportW = 1f
    var viewportH = 1f
}

/**
 * @author squid233
 * @since 0.1.0
 */
class FreeCamera(id: String) : BaseCamera(id) {
    private var yaw = 0f
    private var pitch = 0f

    fun moveRelative(direction: Direction, dt: Float) {
        if (dt == 0f) return

        val siny = sin(yaw)
        val cosy = cosFromSin(siny, yaw)
        val sinx = sin(pitch)
        val cosx = cosFromSin(sinx, pitch)

        when (direction) {
            Direction.WEST -> position.add(-cosy * dt, 0f, siny * dt)
            Direction.EAST -> position.add(cosy * dt, 0f, -siny * dt)
            Direction.DOWN -> position.add(sinx * -siny * dt, -cosx * dt, -sinx * cosy * dt)
            Direction.UP -> position.add(sinx * siny * dt, cosx * dt, sinx * cosy * dt)
            Direction.NORTH -> position.add(cosx * -siny * dt, sinx * dt, -cosx * cosy * dt)
            Direction.SOUTH -> position.add(cosx * siny * dt, -sinx * dt, cosx * cosy * dt)
        }
    }

    fun rotate(yaw: Float, pitch: Float) {
        this.yaw += yaw
        if (this.yaw < 0f) {
            while (this.yaw < 0f)
                this.yaw += RAD360F
        } else if (this.yaw > RAD360F) {
            while (this.yaw > RAD360F)
                this.yaw -= RAD360F
        }

        this.pitch += pitch
        if (this.pitch > RAD90F) this.pitch = RAD90F
        else if (this.pitch < -RAD90F) this.pitch = -RAD90F
    }

    override fun getMatrix(mat: Matrix4f): Matrix4f {
        return mat.rotateX(-pitch).rotateY(-yaw)
            .translate(-position.x, -position.y, -position.z)
    }
}
