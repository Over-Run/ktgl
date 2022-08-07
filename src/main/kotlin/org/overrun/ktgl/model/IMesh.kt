package org.overrun.ktgl.model

import org.overrun.ktgl.gl.GLDrawMode

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vector2(
    val x: Float = 0f,
    val y: Float = 0f
)

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vector3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
)

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vector4(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 0f
)

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vertex(
    val vertex: Vector3 = Vector3(),
    val color0: Vector4? = null,
    val texCoord0: Vector3? = null,
    val normal: Vector3? = null
)

/**
 * Similar to [Vertex] but mutable.
 *
 * @author squid233
 * @since 0.1.0
 */
data class MutableVertex(
    var vertex: Vector3 = Vector3(),
    var color0: Vector4? = null,
    var texCoord0: Vector3? = null,
    var normal: Vector3? = null
) {
    fun toImmutable() = Vertex(vertex, color0, texCoord0, normal)
}

/**
 * @author squid233
 * @since 0.1.0
 */
interface IMesh {
    fun render(mode: GLDrawMode)
    fun getVertices(): List<Vertex>
    fun getLayout(): VertexLayout
}
