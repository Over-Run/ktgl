package org.overrun.ktgl.model.obj

import org.overrun.ktgl.io.IFileProvider
import org.overrun.ktgl.model.*

/**
 * The object model reader.
 *
 * @author squid233
 * @since 0.1.0
 */
class ObjReader {
    private fun exception(args: List<String>, cmdName: String, minCount: Int) =
        IllegalArgumentException("The required components not found: type '$cmdName' required $minCount but expected ${args.size - 1}")

    private fun require(args: List<String>, cmdName: String, minCount: Int): List<String> {
        if (args.size - 1 < minCount)
            throw exception(args, cmdName, minCount)
        return args
    }

    private fun require(index: Int, args: List<String>, cmdName: String, minCount: Int): String {
        if (index >= args.size || args.size - 1 < minCount)
            throw exception(args, cmdName, minCount)
        return args[index]
    }

    private fun <T> MutableMap<String, ArrayList<T>>.addToMap(
        k: String,
        t: T
    ) {
        computeIfAbsent(k) { ArrayList() }.add(t)
    }

    private fun MutableMap<String, ArrayList<MutableVertex>>.addToMap(
        k: String,
        index: Int,
        block: MutableVertex.() -> Unit
    ) {
        computeIfAbsent(k) { ArrayList() }.also {
            if (it.size <= index) {
                MutableVertex().also { v ->
                    it.add(v)
                    block(v)
                }
            } else {
                block(it[index])
            }
        }
    }

    fun load(name: String, layout: VertexLayout, provider: IFileProvider): Model {
//        val materials = HashMap<String, Material>()
        val model = Model()
        val meshes = LinkedHashMap<String, Mesh>()
        val vertices = HashMap<String, ArrayList<MutableVertex>>()
        var posPos = 0
        var texPos = 0
        var nmlPos = 0
        val faces = HashMap<String, ArrayList<Array<Triple<Int, Int?, Int?>>>>()
        var currObjName = "Default"
        provider.useLines(name) { ln ->
            ln.split(Regex("\\s")).also {
                when (it[0]) {
                    "o" -> currObjName = require(1, it, "o", 1)
                    "v" -> require(it, "v", 3).also { args ->
                        vertices.addToMap(
                            currObjName,
                            posPos
                        ) {
                            vertex = Vector3(args[1].toFloat(), args[2].toFloat(), args[3].toFloat())
                        }
                        ++posPos
                    }

                    "vt" -> require(it, "vt", 2).also { args ->
                        vertices.addToMap(
                            currObjName,
                            texPos
                        ) {
                            texCoord0 = Vector3(
                                args[1].toFloat(),
                                args[2].toFloat(),
                                if (args.size > 2) args[3].toFloat() else 0f
                            )
                        }
                        ++texPos
                    }

                    "vn" -> require(it, "vn", 3).also { args ->
                        vertices.addToMap(
                            currObjName,
                            nmlPos
                        ) {
                            normal = Vector3(args[1].toFloat(), args[2].toFloat(), args[3].toFloat())
                        }
                        ++nmlPos
                    }

                    "f" -> require(it, "f", 3).also { args ->
                        val arr = Array(args.size - 1) { i ->
                            val s = args[i + 1].split('/')
                            Triple(
                                s[0].toInt(),
                                if (s.size > 1) s[1].toIntOrNull() else null,
                                if (s.size > 2) s[2].toInt() else null
                            )
                        }
                        faces.addToMap(currObjName, arr)
                    }
                }
            }
        }

        faces.forEach { (name, list) ->
            val vert = vertices[name]!!
            meshes[name] = Mesh(layout) {
                list.forEach {
                    val vertexList = ArrayList<Vertex>()
                    it.forEach { (v, vt, vn) ->
                        vertexList.add(
                            Vertex(
                                vert[v - 1].vertex,
                                texCoord0 = vt?.let { vert[vt - 1].texCoord0 },
                                normal = vn?.let { vert[vn - 1].normal }
                            )
                        )
                    }
                    face(vertexList)
                }
            }
        }

        model.loadMeshes(meshes.values)

        return model
    }
}