package org.overrun.ktgl.gl

import org.lwjgl.opengl.GL11C.*
import java.nio.ByteBuffer

/**
 * GL data types.
 *
 * @author squid233
 * @since 0.1.0
 */
enum class GLDataType(
    val glConst: Int,
    val size: Int,
    val typeName: String
) {
    BYTE(GL_BYTE, 1, "Byte"),
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1, "Unsigned Byte"),
    SHORT(GL_SHORT, 2, "Short"),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2, "Unsigned Short"),
    INT(GL_INT, 4, "Int"),
    UNSIGNED_INT(GL_UNSIGNED_INT, 4, "Unsigned Int"),
    FLOAT(GL_FLOAT, 4, "Float"),
    DOUBLE(GL_DOUBLE, 8, "Double");

    fun put(buffer: ByteBuffer, x: Number) {
        when (x) {
            is Byte -> buffer.put(x)
            is Short -> buffer.putShort(x)
            is Int -> buffer.putInt(x)
            is Float -> buffer.putFloat(x)
            is Double -> buffer.putDouble(x)
        }
    }

    override fun toString(): String = typeName
}
