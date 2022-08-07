package org.overrun.ktgl.model

import org.overrun.ktgl.gl.GLDataType
import org.overrun.ktgl.gl.GLDataType.*
import org.overrun.ktgl.util.math.colorToByte
import org.overrun.ktgl.util.math.normalToByte
import java.nio.ByteBuffer

/**
 * @author squid233
 * @since 0.1.0
 */
enum class VertexElement(
    val size: Int,
    val dataType: GLDataType,
    val normalized: Boolean = false
) {
    POSITION(3, FLOAT) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            vertex.vertex.also { (x, y, z) -> buffer.putFloat(x).putFloat(y).putFloat(z) }
        }
    },
    COLOR0(4, UNSIGNED_BYTE, true) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            vertex.color0?.also { (r, g, b, a) ->
                buffer.put(colorToByte(r)).put(colorToByte(g)).put(colorToByte(b)).put(colorToByte(a))
            }
        }
    },
    COLOR1(4, UNSIGNED_BYTE, true) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            TODO("Not yet implemented")
        }
    },
    COLOR2(4, UNSIGNED_BYTE, true) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            TODO("Not yet implemented")
        }
    },
    TEX_COORD0(3, FLOAT) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            vertex.texCoord0?.also { (s, t, r) -> buffer.putFloat(s).putFloat(t).putFloat(r) }
        }
    },
    TEX_COORD1(3, FLOAT) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            TODO("Not yet implemented")
        }
    },
    TEX_COORD2(3, FLOAT) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            TODO("Not yet implemented")
        }
    },
    NORMAL(3, BYTE, true) {
        override fun put(buffer: ByteBuffer, vertex: Vertex) {
            vertex.normal?.also { (nx, ny, nz) ->
                buffer.put(normalToByte(nx)).put(normalToByte(ny)).put(normalToByte(nz))
            }
        }
    };

    val location = ordinal
    val length: Int = size * dataType.size

    abstract fun put(buffer: ByteBuffer, vertex: Vertex)
}
