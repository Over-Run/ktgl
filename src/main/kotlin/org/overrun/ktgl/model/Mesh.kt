package org.overrun.ktgl.model

/**
 * @author squid233
 * @since 0.1.0
 */
class Mesh() : IMesh {
    private val vertexList = ArrayList<Vertex>()
    private var meshType = MeshType.POSITION

    constructor(block: Mesh.() -> Unit) : this() {
        block()
    }

    constructor(vararg vertices: Array<Vertex>) : this() {
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

    private fun addVertex(vertex: Vertex) {
        vertexList.add(vertex)
        if (meshType != MeshType.POSITION_TEX_NORMAL &&
            vertex.texCoord != null &&
            vertex.normal != null
        ) {
            meshType = MeshType.POSITION_TEX_NORMAL
        } else if (meshType == MeshType.POSITION_TEX_NORMAL) {
            return
        } else {
            val type = MeshType.of(vertex)
            if (type > meshType)
                meshType = type
        }
    }

    override fun getVertices(): List<Vertex> = vertexList
    override fun getType(): MeshType = meshType
}
