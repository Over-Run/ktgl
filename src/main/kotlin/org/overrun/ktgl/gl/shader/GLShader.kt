package org.overrun.ktgl.gl.shader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.lwjgl.opengl.GL20C.*
import org.overrun.ktgl.currentGLStateMgr
import org.overrun.ktgl.io.IFileProvider
import org.overrun.ktgl.util.Identifier
import org.overrun.ktgl.util.ResourceType
import org.overrun.ktgl.util.toFile

const val ATTRIB_POSITION_LOC = 0
const val ATTRIB_COLOR_LOC = 1
const val ATTRIB_TEX_COORD_LOC = 2
const val ATTRIB_NORMAL_LOC = 5

fun bindShader(shader: GLShader): Boolean {
    val stateMgr = currentGLStateMgr
    if (stateMgr != null) {
        stateMgr.program = shader.programId
        return true
    }
    return false
}

fun unbindShader() {
    currentGLStateMgr?.program = 0
}

fun useShader(shader: GLShader, block: GLShader.() -> Unit) {
    currentGLStateMgr?.useProgram(shader.programId) {
        shader.block()
    }
}

object BuiltinShaders {
    const val POSITION_ID = "position"

    @JvmField
    val position = lazy {
        GLShader(POSITION_ID).loadBuiltin("ktgl:shaders/builtin/position.json")
    }
}

/**
 * @author squid233
 * @since 0.1.0
 */
class GLShader(val id: String) : AutoCloseable {
    val programId = glCreateProgram()
    var name: String = "Unknown Shader"
    private var data: GLShaderData? = null
    private val uniformMap = HashMap<CharSequence, GLUniform>()
    private var closed = false

    fun bind(): Boolean {
        val stateMgr = currentGLStateMgr
        if (stateMgr != null) {
            stateMgr.program = programId
            return true
        }
        return false
    }

    fun unbind() {
        currentGLStateMgr?.program = 0
    }

    fun use(block: GLShader.() -> Unit) {
        currentGLStateMgr?.useProgram(programId) {
            block()
        }
    }

    private fun getUniform(block: GLShaderUniforms.() -> GLShaderUniform<*>?): GLUniform? =
        uniformMap[data?.uniforms?.block()?.name!!]

    fun getProjection(): GLUniform? = getUniform { projection }
    fun getView(): GLUniform? = getUniform { view }
    fun getModel(): GLUniform? = getUniform { model }
    fun getColorModulator(): GLUniform? = getUniform { colorModulator }

    fun uploadUniforms() = uniformMap.values.forEach(GLUniform::upload)

    private fun createUniform(name: CharSequence, type: GLUniformType): GLUniform? =
        glGetUniformLocation(programId, name).let { loc ->
            if (loc < 0) null
            else GLUniform(loc, type).also { uniformMap[name] = it }
        }

    private inline fun <T> GLShaderUniform<T>.create(
        type: GLUniformType,
        block: GLUniform.(T) -> Unit
    ) = createUniform(name, type).let {
        if (it != null) {
            if (value != null) it.block(value!!)
        } else {
            throw IllegalStateException("Uniform $type $name not found!")
        }
    }

    private fun GLShaderUniform<FloatArray>.create(type: GLUniformType) =
        create(type, GLUniform::set)

    private fun loadShader(
        resourceType: ResourceType,
        type: Int,
        fileId: String,
        typeName: String,
        provider: IFileProvider
    ): Int = glCreateShader(type).let { shader ->
        glShaderSource(shader, provider.useLines(fileId toFile resourceType))
        glCompileShader(shader)
        if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE)
            throw IllegalStateException(
                "Failed to compile $typeName shader of $id($name): ${glGetShaderInfoLog(shader)}"
            )
        shader
    }

    private fun load(
        type: ResourceType,
        data: GLShaderData,
        provider: IFileProvider
    ): GLShader {
        this.data = data
        name = data.name ?: "Unknown Shader"
        val vsh = loadShader(
            type,
            GL_VERTEX_SHADER,
            "${data.vertex}.vert",
            "vertex",
            provider
        )
        val fsh = loadShader(
            type,
            GL_FRAGMENT_SHADER,
            "${data.fragment}.frag",
            "fragment",
            provider
        )
        glAttachShader(programId, vsh)
        glAttachShader(programId, fsh)
        glBindAttribLocation(programId, ATTRIB_POSITION_LOC, data.input.position.name)
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE)
            throw IllegalStateException("Failed to link program $programId: ${glGetProgramInfoLog(programId)}")
        glDetachShader(programId, vsh)
        glDetachShader(programId, fsh)
        glDeleteShader(vsh)
        glDeleteShader(fsh)

        data.uniforms?.apply {
            projection?.create(GLUniformType.MAT4)
            view?.create(GLUniformType.MAT4)
            model?.create(GLUniformType.MAT4)
            colorModulator?.create(GLUniformType.VEC4)
        }

        return this
    }

    fun load(
        type: ResourceType,
        src: String,
        provider: IFileProvider
    ) = load(type, Json.decodeFromString<GLShaderData>(src), provider)

    @OptIn(ExperimentalSerializationApi::class)
    fun loadFromFile(
        type: ResourceType,
        name: String,
        provider: IFileProvider
    ) = load(type, Json.decodeFromStream<GLShaderData>(provider.toStream(name).getOrThrow()), provider)

    fun loadById(
        type: ResourceType,
        identifier: Identifier,
        provider: IFileProvider
    ) = loadFromFile(type, identifier.toFile(type), provider)

    fun loadById(
        type: ResourceType,
        identifier: String,
        provider: IFileProvider
    ) = loadFromFile(type, identifier toFile type, provider)

    fun loadBuiltin(
        identifier: String
    ) = loadById(ResourceType.ASSETS, identifier, IFileProvider.SYSTEM)

    override fun close() {
        if (closed)
            return
        closed = true
        uniformMap.values.forEach(GLUniform::close)
        currentGLStateMgr?.deleteProgram(programId)
    }
}
