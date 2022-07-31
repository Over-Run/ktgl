package org.overrun.ktgl.gl.shader

import org.lwjgl.opengl.GL20C.*
import org.overrun.ktgl.currentProject
import org.overrun.ktgl.io.IFileProvider
import org.overrun.ktgl.util.Identifier
import org.overrun.ktgl.util.ResourceType

fun bindShader(shader: GLShader): Boolean {
    currentProject?.apply {
        glStateMgr.program = shader.programId
        return true
    }
    return false
}

fun unbindShader() {
    currentProject?.apply {
        glStateMgr.program = 0
    }
}

fun useShader(shader: GLShader, block: () -> Unit) {
    bindShader(shader)
    block()
    unbindShader()
}

/**
 * @author squid233
 * @since 0.1.0
 */
class GLShader : AutoCloseable {
    val programId = glCreateProgram()
    var name: String? = null

    private fun loadShader(type: Int, src: String, typeName: String): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, src)
        glCompileShader(shader)
        if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE)
            throw IllegalStateException("Failed to compile $typeName shader: ${glGetShaderInfoLog(shader)}")
        return shader
    }

    private fun load(data: GLShaderData) {
        name = data.name
        val vsh = loadShader(
            GL_VERTEX_SHADER,
            IFileProvider.ofCaller().useLines(Identifier.toFile(ResourceType.ASSETS, data.vertex)),
            "vertex"
        )
    }

    override fun close() {
        glDeleteProgram(programId)
    }
}
