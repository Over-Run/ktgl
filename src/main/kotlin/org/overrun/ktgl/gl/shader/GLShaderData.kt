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
    val position: GLShaderAttrib,
    val color0: GLShaderAttrib? = null
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
internal data class GLShaderUniformInt(
    override val name: String,
    override val value: Int? = 0
) : GLShaderUniform<Int>()

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderUniformFloat(
    override val name: String,
    override val value: Float? = 0f
) : GLShaderUniform<Float>()

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal abstract class GLShaderUniformFArr : GLShaderUniform<FloatArray>()

/**
 * @author squid233
 * @since 0.1.0
 */
@Serializable
internal data class GLShaderUniformMat3(
    override val name: String,
    override val value: FloatArray? = floatArrayOf(
        1f, 0f, 0f,
        0f, 1f, 0f,
        0f, 0f, 1f
    )
) : GLShaderUniformFArr() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GLShaderUniformMat3

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
internal data class GLShaderUniformMat4(
    override val name: String,
    override val value: FloatArray? = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
) : GLShaderUniformFArr() {
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
internal data class GLShaderUniformVec3(
    override val name: String,
    override val value: FloatArray? = floatArrayOf(0f, 0f, 0f)
) : GLShaderUniformFArr() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GLShaderUniformVec3

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
    override val value: FloatArray? = floatArrayOf(0f, 0f, 0f, 0f)
) : GLShaderUniformFArr() {
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
    val modelview: GLShaderUniformMat4? = null,
    val normal: GLShaderUniformMat3? = null,
    val colorModulator: GLShaderUniformVec4? = null,
    val deltaTime: GLShaderUniformFloat? = null,
    val currTime: GLShaderUniformFloat? = null,
    val sampler0: GLShaderUniformInt? = null
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
