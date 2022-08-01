package org.overrun.ktgl.gl.shader

import org.joml.Matrix4fc
import org.joml.Vector4fc
import org.lwjgl.opengl.GL20C.glUniform4f
import org.lwjgl.opengl.GL20C.nglUniformMatrix4fv
import org.lwjgl.system.MemoryUtil.*
import java.nio.ByteBuffer

enum class GLUniformType(val size: Int, val length: Int, val glName: String) {
    VEC4(4, 16, "vec4"),
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

    fun set(array: FloatArray) = set { array.forEach(it::putFloat) }

    fun set(vec4: Vector4fc) = set(vec4::get)

    fun set(mat4: Matrix4fc) = set(mat4::get)

    fun upload() {
        if (!dirty)
            return
        dirty = false
        when (type) {
            GLUniformType.VEC4 -> glUniform4f(
                location,
                buffer.getFloat(0),
                buffer.getFloat(4),
                buffer.getFloat(8),
                buffer.getFloat(12)
            )

            GLUniformType.MAT4 -> nglUniformMatrix4fv(location, 1, false, memAddress(buffer))
        }
    }

    override fun close() {
        memFree(buffer)
    }
}
