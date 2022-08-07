package org.overrun.ktgl.asset

import org.lwjgl.opengl.GL12C.*

/**
 * @author squid233
 * @since 0.1.0
 */
data class TextureParam(
    val minFilter: Int = GL_NEAREST_MIPMAP_LINEAR,
    val magFilter: Int = GL_LINEAR,
    val wrapS: Int = GL_REPEAT,
    val wrapT: Int = GL_REPEAT,
    val wrapR: Int = GL_REPEAT,
) {
    fun apply(target: Int) {
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter)
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter)
        glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS)
        glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT)
        glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR)
    }
}
