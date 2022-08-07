package org.overrun.ktgl.gl.shader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.lwjgl.opengl.GL20C.*
import org.overrun.ktgl.currentGLStateMgr
import org.overrun.ktgl.io.IFileProvider
import org.overrun.ktgl.model.VertexElement
import org.overrun.ktgl.util.Identifier
import org.overrun.ktgl.util.ResourceType
import org.overrun.ktgl.util.toFile

fun unbindShader() {
    currentGLStateMgr?.program = 0
}

object BuiltinShaders {
    const val POSITION_ID = "position"
    const val SKYBOX_CUBEMAP_ID = "skybox-cubemap"

    @JvmField
    val position = lazy {
        GLShader(POSITION_ID).loadBuiltin("shaders/builtin/position.json")
    }

    @JvmField
    val skyboxCubemap = lazy {
        GLShader(SKYBOX_CUBEMAP_ID).loadBuiltin("shaders/skybox/cubemap.json")
    }
}

/**
 * ## The GL Shader
 *
 * The GL shader use to render scenes.
 *
 * Every game objects must be bind to a shader else they will not be rendered, even they are visible.
 *
 * ### JSON Format
 *
 * Any mistakes will cause exception.
 *
 * ```json
 * {
 *   "name": "Shader Name",
 *   "vertex": "namespace:path/to/shader",
 *   "fragment": "namespace:path/to/shader",
 *   "input": {
 *     "position": {
 *       "name": "AttribNameInShader"
 *     }
 *   },
 *   // optional
 *   "uniforms": {
 *     // optional
 *     "uniformName": {
 *       "name": "UniformNameInShader",
 *       // optional
 *       "value": Any
 *     }
 *   }
 * }
 * ```
 *
 * ### Available Uniforms
 *
 * - `type name`
 * - `mat4 projection`
 * - `mat4 modelview`
 * - `mat4 normal`
 * - `vec4 colorModulator`
 * - `float deltaTime`
 * - `float currTime`
 * - `gsampler sampler0`
 *
 * @param[id] the shader id
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
            val pId = stateMgr.program
            if (pId == programId) {
                return false
            }
            stateMgr.program = programId
            return true
        }
        return false
    }

    fun use(block: GLShader.() -> Unit) {
        currentGLStateMgr?.useProgram(programId) {
            block()
        }
    }

    private fun getUniform(block: GLShaderUniforms.() -> GLShaderUniform<*>?): GLUniform? {
        val d = data ?: return null
        val uniforms = d.uniforms ?: return null
        val uniform = block(uniforms) ?: return null
        return uniformMap[uniform.name]
    }

    fun getProjection(): GLUniform? = getUniform { projection }
    fun getModelview(): GLUniform? = getUniform { modelview }
    fun getNormal(): GLUniform? = getUniform { normal }
    fun getColorModulator(): GLUniform? = getUniform { colorModulator }
    fun getDeltaTime(): GLUniform? = getUniform { deltaTime }
    fun getCurrTime(): GLUniform? = getUniform { currTime }
    fun getSampler0(): GLUniform? = getUniform { sampler0 }

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

    private fun GLShaderUniformInt.create(type: GLUniformType) =
        create(type, GLUniform::set)

    private fun GLShaderUniformFloat.create(type: GLUniformType) =
        create(type, GLUniform::set)

    private fun GLShaderUniformFArr.create(type: GLUniformType) =
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
        data.input.apply {
            glBindAttribLocation(programId, VertexElement.POSITION.location, position.name)
            color0?.apply { glBindAttribLocation(programId, VertexElement.COLOR0.location, name) }
        }
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE)
            throw IllegalStateException("Failed to link program $programId: ${glGetProgramInfoLog(programId)}")
        glDetachShader(programId, vsh)
        glDetachShader(programId, fsh)
        glDeleteShader(vsh)
        glDeleteShader(fsh)

        data.uniforms?.apply {
            projection?.create(GLUniformType.MAT4)
            modelview?.create(GLUniformType.MAT4)
            normal?.create(GLUniformType.MAT3)
            colorModulator?.create(GLUniformType.VEC4)
            deltaTime?.create(GLUniformType.FLOAT)
            currTime?.create(GLUniformType.FLOAT)
            sampler0?.create(GLUniformType.INT)
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
