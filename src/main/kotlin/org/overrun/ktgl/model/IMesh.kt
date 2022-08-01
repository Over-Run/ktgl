package org.overrun.ktgl.model

typealias Vector2 = Pair<Float, Float>
typealias Vector3 = Triple<Float, Float, Float>

/**
 * @author squid233
 * @since 0.1.0
 */
interface IMesh {
    val positions: List<Vector3>
    val texCoords: List<Vector2>?
    val normals: List<Vector3>?
}
