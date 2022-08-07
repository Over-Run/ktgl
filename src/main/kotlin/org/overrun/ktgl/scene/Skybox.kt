package org.overrun.ktgl.scene

import org.overrun.ktgl.Project
import org.overrun.ktgl.asset.TextureCubeMap
import org.overrun.ktgl.gl.GLDrawMode
import org.overrun.ktgl.model.Model
import org.overrun.ktgl.model.POSITION
import org.overrun.ktgl.model.Vector3
import org.overrun.ktgl.model.Vertex
import org.overrun.ktgl.util.math.*

/**
 * @author squid233
 * @since 0.1.0
 */
interface ISkybox : AutoCloseable {
    fun render(project: Project, scene: Scene)
}

/**
 * @author squid233
 * @since 0.1.0
 */
class SkyboxCubeMap : ISkybox {
    private var closed = false
    val model = lazy(LazyThreadSafetyMode.NONE) {
        Model(POSITION) {
            val v0 = Vertex(Vector3(-1f, 1f, 1f))
            val v1 = Vertex(Vector3(-1f, -1f, 1f))
            val v2 = Vertex(Vector3(-1f, -1f, -1f))
            val v3 = Vertex(Vector3(-1f, 1f, -1f))
            val v4 = Vertex(Vector3(1f, 1f, -1f))
            val v5 = Vertex(Vector3(1f, -1f, -1f))
            val v6 = Vertex(Vector3(1f, -1f, 1f))
            val v7 = Vertex(Vector3(1f, 1f, 1f))
            // -x
            face(v0, v1, v2, v3)
            // +x
            face(v4, v5, v6, v7)
            // -y
            face(v2, v1, v6, v5)
            // +y
            face(v0, v3, v4, v7)
            // -z
            face(v3, v2, v5, v4)
            // +z
            face(v7, v6, v1, v0)
        }
    }
    var texture: Lazy<TextureCubeMap?> = lazyOf(null)

    override fun render(project: Project, scene: Scene) {
        texture.value?.apply {
            project.glStateMgr.useTextureCubeMap(id) {
                project.createBuiltinShader { skyboxCubemap }.use {
                    getProjection()?.set(ktglProjMat)
                    ktglViewMat {
                        scene.currCamera?.getMatrixNoTrans(identity())
                        ktglModelMat.identity()
                        getModelview()?.set(ktglModelviewMat)
                        uploadUniforms()
                    }
                    uploadUniforms()
                    model.value.render(GLDrawMode.TRIANGLES)
                }
            }
        }
    }

    override fun close() {
        if (closed)
            return
        closed = true
        if (model.isInitialized()) model.value.close()
    }
}
