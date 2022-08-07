package org.overrun.ktgl.model

val POSITION = VertexLayout(VertexElement.POSITION)
val POSITION_COLOR0 = VertexLayout(VertexElement.POSITION, VertexElement.COLOR0)
val POSITION_NORMAL = VertexLayout(VertexElement.POSITION, VertexElement.NORMAL)

/**
 * The vertex element layout.
 *
 * @author squid233
 * @since 0.1.0
 */
class VertexLayout() {
    private val elements = ArrayList<Pair<VertexElement, Int>>()
    var stride = 0
        private set

    constructor(vararg elements: VertexElement) : this() {
        elements.forEach(::addElement)
    }

    fun addElement(element: VertexElement) {
        elements.add(element to stride)
        stride += element.length
    }

    fun forEachElement(action: (Pair<VertexElement, Int>) -> Unit) {
        elements.forEach(action)
    }
}
