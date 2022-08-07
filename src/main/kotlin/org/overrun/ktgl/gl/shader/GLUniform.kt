package org.overrun.ktgl.gl.shader

import org.joml.Matrix3fc
import org.joml.Matrix4fc
import org.joml.Vector3fc
import org.joml.Vector4fc
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.system.MemoryUtil.*
import java.nio.ByteBuffer

enum class GLUniformType(val size: Int, val length: Int, val glName: String) {
    INT(1, 4, "int"),
    FLOAT(1, 4, "float"),
    VEC3(3, 12, "vec3"),
    VEC4(4, 16, "vec4"),
    MAT3(9, 36, "mat3"),
    MAT4(16, 64, "mat4");

    override fun toString(): String {
        return glName
    }
}

/**
 * @author squid233
 * @since 0.1.0
 */
class GLUniform(val location: Int, val type: GLUniformType) : AutoCloseable {
    private val buffer: ByteBuffer = memAlloc(type.length)
    private var dirty = true

    private fun markDirty() {
        dirty = true
    }

    fun set(block: (ByteBuffer) -> Unit) {
        markDirty()
        block(buffer.position(0))
        buffer.position(0)
    }

    fun set(x: Int) = set { it.putInt(x) }

    fun set(x: Float) = set { it.putFloat(x) }
    fun set(x: Float, y: Float) = set { it.putFloat(x).putFloat(y) }
    fun set(x: Float, y: Float, z: Float) = set { it.putFloat(x).putFloat(y).putFloat(z) }
    fun set(x: Float, y: Float, z: Float, w: Float) = set { it.putFloat(x).putFloat(y).putFloat(z).putFloat(w) }

    fun set(array: FloatArray) = set { array.forEach(it::putFloat) }

    fun set(vec3: Vector3fc) = set(vec3::get)
    fun set(vec4: Vector4fc) = set(vec4::get)

    fun set(mat3: Matrix3fc) = set(mat3::get)
    fun set(mat4: Matrix4fc) = set(mat4::get)

    fun upload() {
        if (!dirty)
            return
        dirty = false
        when (type) {
            GLUniformType.INT -> glUniform1i(location, buffer.getInt(0))
            GLUniformType.FLOAT -> glUniform1f(location, buffer.getFloat(0))
            GLUniformType.VEC3 -> glUniform3f(
                location,
                buffer.getFloat(0),
                buffer.getFloat(4),
                buffer.getFloat(8)
            )

            GLUniformType.VEC4 -> glUniform4f(
                location,
                buffer.getFloat(0),
                buffer.getFloat(4),
                buffer.getFloat(8),
                buffer.getFloat(12)
            )

            GLUniformType.MAT3 -> nglUniformMatrix3fv(location, 1, false, memAddress(buffer))
            GLUniformType.MAT4 -> nglUniformMatrix4fv(location, 1, false, memAddress(buffer))
        }
    }

    override fun close() {
        memFree(buffer)
    }
}
