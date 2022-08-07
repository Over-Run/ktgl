package org.overrun.ktgl.asset

import org.lwjgl.opengl.GL13C.*
import org.overrun.ktgl.currentGLStateMgr
import org.overrun.ktgl.io.IFileProvider
import org.overrun.ktgl.util.math.Direction

/**
 * A cube map texture.
 *
 * @author squid233
 * @since 0.1.0
 */
class TextureCubeMap(var param: TextureParam?):AutoCloseable {
    val id: Int = glGenTextures()

    constructor(
        sides: Map<Direction, String>,
        param: TextureParam?,
        provider: IFileProvider
    ) : this(param) {
        load(sides, provider)
    }

    fun load(
        sides: Map<Direction, String>,
        provider: IFileProvider
    ) {
        currentGLStateMgr!!.useTextureCubeMap(id) {
            sides.forEach { (dir, name) ->
                NativeImage(name, provider).use { img ->
                    glTexImage2D(
                        dir.cubeMapId,
                        0,
                        GL_RGBA,
                        img.width,
                        img.height,
                        0,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        img.buffer
                    )
                }
            }
            param?.apply(GL_TEXTURE_CUBE_MAP)
        }
    }

    override fun close() {
        glDeleteTextures(id)
    }
}
