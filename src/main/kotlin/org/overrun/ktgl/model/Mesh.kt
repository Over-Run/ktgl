package org.overrun.ktgl.model

import org.lwjgl.opengl.GL30C.*
import org.lwjgl.system.MemoryUtil.*
import org.overrun.ktgl.currentGLStateMgr
import org.overrun.ktgl.gl.GLDrawMode
import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * @param[vertexLayout] the vertex layout
 * @param[immutable] will the mesh immutable
 * @author squid233
 * @since 0.1.0
 */
class Mesh(
    val vertexLayout: VertexLayout = VertexLayout(),
    val immutable: Boolean = true
) : IMesh, AutoCloseable {
    private val vertexList = ArrayList<Vertex>()
    private val vertices = ArrayList<Vertex>()
    private val indices = ArrayList<Int>()
    private var vao = 0
    private var vbo = 0
    private var ebo = 0
    private var loaded = false
    private var buffer: ByteBuffer? = null
    private var indexBuffer: IntBuffer? = null

    constructor(
        vertexLayout: VertexLayout = VertexLayout(),
        immutable: Boolean = true,
        block: Mesh.() -> Unit
    ) : this(vertexLayout, immutable) {
        block()
    }

    constructor(
        vertexLayout: VertexLayout = VertexLayout(),
        immutable: Boolean = true,
        vararg vertices: Array<Vertex>
    ) : this(vertexLayout, immutable) {
        vertices.forEach { face(*it) }
    }



    fun face(vararg vertices: Vertex): Mesh {
        val numTri = (vertices.size - 2).coerceAtLeast(0)
        if (numTri > 0) {
            var nextIndex = 1
            for (i in 0 until numTri) {
                addVertex(vertices[0])
                addVertex(vertices[nextIndex])
                addVertex(vertices[nextIndex + 1])
                ++nextIndex
            }
        }
        return this
    }

    fun faceNoTess(vararg vertices: Vertex): Mesh {
        vertices.forEach(::addVertex)
        return this
    }

    fun face(vertices: List<Vertex>): Mesh {
        val numTri = (vertices.size - 2).coerceAtLeast(0)
        if (numTri > 0) {
            var nextIndex = 1
            for (i in 0 until numTri) {
                addVertex(vertices[0])
                addVertex(vertices[nextIndex])
                addVertex(vertices[nextIndex + 1])
                ++nextIndex
            }
        }
        return this
    }

    fun faceNoTess(vertices: Iterable<Vertex>): Mesh {
        vertices.forEach(::addVertex)
        return this
    }

    private fun addVertex(vertex: Vertex) {
        vertexList.add(vertex)
    }

    fun load() {
        if (immutable && loaded) throw IllegalStateException("The mesh is immutable!")
        loaded = true
        vertices.clear()
        indices.clear()

        getVertices().forEach {
            val index = vertices.indexOf(it)
            if (index >= 0) {
                indices.add(index)
            } else {
                vertices.add(it)
                indices.add(vertices.indexOf(it))
            }
        }

        var bufResized = false
        var indexBufResized = false

        val bufferSize = vertexLayout.stride * vertices.size
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
            vertices.forEach {
                vertexLayout.forEachElement { (element, _) ->
                    element.put(this, it)
                }
            }
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
            vertexLayout.forEachElement { (element, offset) ->
                glEnableVertexAttribArray(element.location)
                glVertexAttribPointer(
                    element.location,
                    element.size,
                    element.dataType.glConst,
                    element.normalized,
                    vertexLayout.stride,
                    Integer.toUnsignedLong(offset)
                )
            }
        }
    }

    override fun render(mode: GLDrawMode) {
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

    override fun getVertices(): List<Vertex> = vertexList
    override fun getLayout(): VertexLayout = vertexLayout
}
