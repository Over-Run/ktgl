package org.overrun.ktgl.model

import org.joml.Vector3f

/**
 * @author squid233
 * @since 0.1.0
 */
class Material {
    val ambient = Vector3f()
    val diffuse = Vector3f()
    val specular = Vector3f()

    /**
     * The specular exponent multiplied by 128f.
     */
    var shininess = 0f
}
