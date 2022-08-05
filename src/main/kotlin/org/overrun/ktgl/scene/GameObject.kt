package org.overrun.ktgl.scene

import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector4f
import org.overrun.ktgl.gl.GLDrawMode
import org.overrun.ktgl.gl.shader.GLShader
import org.overrun.ktgl.model.IModel

/**
 * ## Game Object
 *
 * @author squid233
 * @since 0.1.0
 */
open class GameObject(val id: String) {
    var name = id
    var behavior: Behavior? = null
    var visible = true
    var model: Lazy<IModel?> = lazyOf(null)
    var shader: () -> GLShader? = { null }
    var drawType = GLDrawMode.TRIANGLES

    /**
     * The rotation relative center
     */
    val anchor = Vector3f()
    val position = Vector3f()
    val rotation = Quaternionf()
    val scale = Vector3f(1f)

    val color = Vector4f(1f)

    fun behavior(block: Behavior.() -> Unit) {
        val behavior = Behavior()
        block(behavior)
        this.behavior = behavior
    }

    fun model(block: () -> IModel?) {
        // TODO: Thread safe
        model = lazy(LazyThreadSafetyMode.NONE, block)
    }

    fun shader(block: () -> GLShader?) {
        shader = block
    }
}
