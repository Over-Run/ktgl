package org.overrun.ktgl.model

import org.overrun.ktgl.gl.GLDrawMode

/**
 * @author squid233
 * @since 0.1.0
 */
class Model() : IModel, AutoCloseable {
    private val meshes = ArrayList<IMesh>()

    constructor(
        layout: VertexLayout,
        block: Mesh.() -> Unit
    ) : this() {
        loadMesh(layout, block)
    }

    fun addMeshes(vararg meshes: IMesh): Model {
        meshes.forEach {
            this.meshes.add(it)
            if (it is Mesh) {
                it.load()
            }
        }
        return this
    }

    fun loadMeshes(vararg meshes: IMesh): Model {
        close()
        this.meshes.clear()
        addMeshes(*meshes)
        return this
    }

    fun addMeshes(meshes: Iterable<IMesh>): Model {
        meshes.forEach {
            this.meshes.add(it)
            if (it is Mesh) {
                it.load()
            }
        }
        return this
    }

    fun loadMeshes(meshes: Iterable<IMesh>): Model {
        close()
        this.meshes.clear()
        addMeshes(meshes)
        return this
    }

    fun loadMesh(
        layout: VertexLayout,
        block: Mesh.() -> Unit
    ): Model = loadMeshes(Mesh(layout, block = block))

    fun forEachMesh(action: (IMesh) -> Unit) {
        meshes.forEach(action)
    }

    override fun render(mode: GLDrawMode) {
        forEachMesh { it.render(mode) }
    }

    override fun close() {
        forEachMesh { if (it is AutoCloseable) it.close() }
    }
}
