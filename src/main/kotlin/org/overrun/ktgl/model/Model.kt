package org.overrun.ktgl.model

import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil.*
import org.overrun.ktgl.currentGLStateMgr
import org.overrun.ktgl.gl.GLDrawMode
import org.overrun.ktgl.gl.shader.ATTRIB_NORMAL_LOC
import org.overrun.ktgl.gl.shader.ATTRIB_POSITION_LOC
import org.overrun.ktgl.gl.shader.ATTRIB_TEX_COORD_LOC
import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * @param[immutable] will the model immutable
 * @author squid233
 * @since 0.1.0
 */
class Model(val immutable: Boolean = true) : IModel, AutoCloseable {
    private val typeModelMap = HashMap<MeshType, TypeModel>()

    /**
     * @param[type] the mesh type
     * @author squid233
     * @since 0.1.0
     */
    private inner class TypeModel(val type: MeshType) : AutoCloseable {
        private val vertices = ArrayList<Vertex>()
        private val indices = ArrayList<Int>()
        private var vao = 0
        private var vbo = 0
        private var ebo = 0
        private var loaded = false
        private var buffer: ByteBuffer? = null
        private var indexBuffer: IntBuffer? = null

        fun addMeshes(vararg meshes: IMesh) {
            if (immutable) throw IllegalStateException("The model is immutable!")
            if (!loaded) {
                loadMeshes(*meshes)
                return
            }
            if (meshes.isEmpty()) {
                return
            }

            meshes.forEach { mesh ->
                mesh.getVertices().forEach {
                    val index = vertices.indexOf(it)
                    if (index >= 0) {
                        indices.add(index)
                    } else {
                        vertices.add(it)
                        indices.add(vertices.indexOf(it))
                    }
                }
            }

            buffer = memRealloc(buffer, type.stride * vertices.size)
            indexBuffer = memRealloc(indexBuffer, indices.size)

            vertices.forEach { type.processor(buffer!!.clear(), it) }
            buffer!!.flip()
            indices.forEach(indexBuffer!!.clear()::put)
            indexBuffer!!.flip()

            currentGLStateMgr?.useVertexArray(vao) {
                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                nglBufferData(
                    GL_ARRAY_BUFFER,
                    buffer!!.capacity().toLong(),
                    memAddress(buffer!!),
                    GL_DYNAMIC_DRAW
                )
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
                nglBufferData(
                    GL_ELEMENT_ARRAY_BUFFER,
                    indexBuffer!!.capacity().toLong().shl(2),
                    memAddress(indexBuffer!!),
                    GL_DYNAMIC_DRAW
                )
            }
        }

        fun loadMeshes(vararg meshes: IMesh) {
            if (immutable && loaded) throw IllegalStateException("The model is immutable!")
            loaded = true
            vertices.clear()
            indices.clear()

            meshes.forEach { mesh ->
                mesh.getVertices().forEach {
                    val index = vertices.indexOf(it)
                    if (index >= 0) {
                        indices.add(index)
                    } else {
                        vertices.add(it)
                        indices.add(vertices.indexOf(it))
                    }
                }
            }

            var bufResized = false
            var indexBufResized = false

            val bufferSize = type.stride * vertices.size
            if (buffer == null) {
                buffer = memAlloc(bufferSize)
                bufResized = true
            } else if (!immutable && buffer!!.capacity() < bufferSize) {
                buffer = memRealloc(buffer, bufferSize)
                bufResized = true
            }

            if (indexBuffer == null) {
                indexBuffer = memAllocInt(indices.size)
                indexBufResized = true
            } else if (!immutable && indexBuffer!!.capacity() < indices.size) {
                indexBuffer = memRealloc(indexBuffer, indices.size)
                indexBufResized = true
            }

            buffer!!.apply {
                clear()
                vertices.forEach { type.processor(this, it) }
                flip()
            }
            indexBuffer!!.apply {
                clear()
                indices.forEach(::put)
                flip()
            }

            if (vao == 0) vao = glGenVertexArrays()
            if (vbo == 0) vbo = glGenBuffers()
            if (ebo == 0) ebo = glGenBuffers()
            currentGLStateMgr?.useVertexArray(vao) {
                glBindBuffer(GL_ARRAY_BUFFER, vbo)
                if (bufResized) {
                    nglBufferData(
                        GL_ARRAY_BUFFER,
                        buffer!!.capacity().toLong(),
                        memAddress(buffer!!),
                        if (immutable) GL_STATIC_DRAW else GL_DYNAMIC_DRAW
                    )
                } else {
                    glBufferSubData(GL_ARRAY_BUFFER, 0L, buffer!!)
                }
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
                if (indexBufResized) {
                    nglBufferData(
                        GL_ELEMENT_ARRAY_BUFFER,
                        indexBuffer!!.capacity().toLong().shl(2),
                        memAddress(indexBuffer!!),
                        if (immutable) GL_STATIC_DRAW else GL_DYNAMIC_DRAW
                    )
                } else {
                    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0L, indexBuffer!!)
                }
                glEnableVertexAttribArray(ATTRIB_POSITION_LOC)
                glVertexAttribPointer(
                    ATTRIB_POSITION_LOC,
                    3,
                    GL_FLOAT,
                    false,
                    type.stride,
                    0L
                )
                if (type.hasTexCoord) {
                    glEnableVertexAttribArray(ATTRIB_TEX_COORD_LOC)
                    glVertexAttribPointer(
                        ATTRIB_TEX_COORD_LOC,
                        2,
                        GL_FLOAT,
                        false,
                        type.stride,
                        12L
                    )
                }
                if (type.hasNormal) {
                    glEnableVertexAttribArray(ATTRIB_NORMAL_LOC)
                    glVertexAttribPointer(
                        ATTRIB_NORMAL_LOC,
                        3,
                        GL_BYTE,
                        true,
                        type.stride,
                        if (type.hasTexCoord) 20L else 12L
                    )
                }
            }
        }

        fun render(mode: GLDrawMode) {
            if (vao != 0) {
                currentGLStateMgr?.useVertexArray(vao) {
                    glDrawElements(mode.glConst, indices.size, GL_UNSIGNED_INT, 0L)
                }
            }
        }

        override fun close() {
            memFree(buffer)
            memFree(indexBuffer)
            currentGLStateMgr?.deleteVertexArray(vao)
            glDeleteBuffers(vbo)
            glDeleteBuffers(ebo)
        }
    }

    private fun processMeshes(
        predicate: List<IMesh>.(MeshType) -> Boolean,
        meshes: Array<out IMesh>,
        block: TypeModel.(Array<out IMesh>) -> Unit
    ) {
        MeshType.values().forEach { type ->
            meshes.filter { it.getType() == type }.also {
                if (it.predicate(type)) {
                    processMeshes(type, it, block)
                }
            }
        }

    }

    private fun processMeshes(
        type: MeshType,
        list: List<IMesh>,
        block: TypeModel.(Array<out IMesh>) -> Unit
    ) {
        typeModelMap.computeIfAbsent(type) { TypeModel(type) }.block(list.toTypedArray())
    }

    fun addMeshes(vararg meshes: IMesh): Model {
        if (meshes.isEmpty())
            return this
        // TODO: May cause bug
        processMeshes({ isNotEmpty() }, meshes, TypeModel::addMeshes)
        return this
    }

    fun loadMeshes(vararg meshes: IMesh): Model {
        typeModelMap.clear()
        // TODO: May cause bug
        processMeshes({ isNotEmpty() || typeModelMap.containsKey(it) }, meshes, TypeModel::loadMeshes)
        return this
    }

    override fun render(mode: GLDrawMode) {
        typeModelMap.values.forEach { it.render(mode) }
    }

    override fun close() {
        typeModelMap.values.forEach(TypeModel::close)
    }
}
