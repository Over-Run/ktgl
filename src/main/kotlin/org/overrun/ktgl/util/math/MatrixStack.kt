package org.overrun.ktgl.util.math

import org.joml.Matrix3fStack
import org.joml.Matrix4fStack

val ktglProjMat = Matrix4fStack(4)
val ktglViewMat = Matrix4fStack(32)
val ktglModelMat = Matrix4fStack(32)
val ktglModelviewMat = Matrix4fStack(32)
    get() = ktglViewMat(field) { mul(ktglModelMat).get(field) }
val ktglNormalMat = Matrix3fStack(32)
    get() = field.also { ktglModelviewMat.normal(it) }

inline operator fun <T> Matrix4fStack.invoke(
    mat: T,
    block: Matrix4fStack.() -> Unit
): T {
    pushMatrix().block()
    popMatrix()
    return mat
}

inline operator fun Matrix4fStack.invoke(block: Matrix4fStack.() -> Unit)
    : Matrix4fStack {
    pushMatrix().block()
    return popMatrix()
}
