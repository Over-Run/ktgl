package org.overrun.ktgl.io

import org.lwjgl.system.CustomBuffer
import org.lwjgl.system.MemoryUtil
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun FloatBuffer.put(vararg src: Float): FloatBuffer {
    src.forEach(::put)
    return this
}

@OptIn(ExperimentalContracts::class)
fun <T : Buffer?, R> T.use(block: (T) -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block(this)
    } finally {
        MemoryUtil.memFree(this)
    }
}

@OptIn(ExperimentalContracts::class)
fun <T : CustomBuffer<*>?, R> T.use(block: (T) -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    try {
        return block(this)
    } finally {
        MemoryUtil.memFree(this)
    }
}
