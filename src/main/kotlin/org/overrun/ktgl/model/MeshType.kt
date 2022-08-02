package org.overrun.ktgl.model

import org.overrun.ktgl.util.math.normalToByte
import java.nio.ByteBuffer

/**
 * @author squid233
 * @since 0.1.0
 */
enum class MeshType(
    val stride: Int,
    val hasTexCoord: Boolean = false,
    val hasNormal: Boolean = false,
    val processor: ByteBuffer.(Vertex) -> Unit
) {
    POSITION(12, processor = { (vertex) ->
        vertex.let { (x, y, z) -> putFloat(x).putFloat(y).putFloat(z) }
    }),
    POSITION_TEX(20, true, processor = { (vertex, texCoords) ->
        vertex.let { (x, y, z) -> putFloat(x).putFloat(y).putFloat(z) }
        texCoords!!.let { (u, v) -> putFloat(u).putFloat(v) }
    }),
    POSITION_NORMAL(15, hasNormal = true, processor = { (vertex, _, normal) ->
        vertex.let { (x, y, z) -> putFloat(x).putFloat(y).putFloat(z) }
        normal!!.let { (nx, ny, nz) -> put(normalToByte(nx)).put(normalToByte(ny)).put(normalToByte(nz)) }
    }),
    POSITION_TEX_NORMAL(23, true, true, { (vertex, texCoords, normal) ->
        vertex.let { (x, y, z) -> putFloat(x).putFloat(y).putFloat(z) }
        texCoords!!.let { (u, v) -> putFloat(u).putFloat(v) }
        normal!!.let { (nx, ny, nz) -> put(normalToByte(nx)).put(normalToByte(ny)).put(normalToByte(nz)) }
    });

    companion object {
        @JvmStatic
        fun of(texCoord: Any?, normal: Any?): MeshType =
            if (texCoord != null) {
                if (normal != null) POSITION_TEX_NORMAL else POSITION_TEX
            } else if (normal != null) POSITION_NORMAL else POSITION

        @JvmStatic
        fun of(vertex: Vertex): MeshType = of(vertex.texCoord, vertex.normal)
    }
}
