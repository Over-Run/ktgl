package org.overrun.ktgl.gl.shader

import kotlinx.serialization.Serializable

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderAttrib(
    val name: String
)

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderInput(
    val position: GLShaderAttrib
)

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal abstract class GLShaderUniform<T> {
    abstract val name: String
    abstract val value: T?
}

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderUniformMat4(
    override val name: String,
    override val value: FloatArray? = floatArrayOf(
        1F, 0F, 0F, 0F,
        0F, 1F, 0F, 0F,
        0F, 0F, 1F, 0F,
        0F, 0F, 0F, 1F
    )
) : GLShaderUniform<FloatArray>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GLShaderUniformMat4

        if (name != other.name) return false
        if (value != null) {
            if (other.value == null) return false
            if (!value.contentEquals(other.value)) return false
        } else if (other.value != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (value?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderUniformVec4(
    override val name: String,
    override val value: FloatArray? = floatArrayOf(0F, 0F, 0F, 0F)
) : GLShaderUniform<FloatArray>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GLShaderUniformVec4

        if (name != other.name) return false
        if (value != null) {
            if (other.value == null) return false
            if (!value.contentEquals(other.value)) return false
        } else if (other.value != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (value?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderUniforms(
    val projection: GLShaderUniformMat4? = null,
    val view: GLShaderUniformMat4? = null,
    val model: GLShaderUniformMat4? = null,
    val colorModulator: GLShaderUniformVec4? = null
)

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderData(
    val name: String? = null,
    val vertex: String,
    val fragment: String,
    val input: GLShaderInput,
    val uniforms: GLShaderUniforms? = null
)
