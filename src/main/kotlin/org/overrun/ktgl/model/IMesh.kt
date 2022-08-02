package org.overrun.ktgl.model

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vector2(
    val x: Float = 0F,
    val y: Float = 0F
)

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vector3(
    val x: Float = 0F,
    val y: Float = 0F,
    val z: Float = 0F
)

/**
 * @author squid233
 * @since 0.1.0
 */
data class Vertex(
    val vertex: Vector3 = Vector3(),
    val texCoord: Vector2? = null,
    val normal: Vector3? = null
) {
    constructor(
        x: Float = 0F, y: Float = 0F, z: Float = 0F,
        u: Float? = null, v: Float? = null,
        nx: Float? = null, ny: Float? = null, nz: Float? = null
    ) : this(
        Vector3(x, y, z),
        if (u != null && v != null) Vector2(u, v) else null,
        if (nx != null && ny != null && nz != null) Vector3(nx, ny, nz) else null
    )
}

/**
 * @author squid233
 * @since 0.1.0
 */
interface IMesh {
    fun getVertices(): List<Vertex>
    fun getType(): MeshType
}
