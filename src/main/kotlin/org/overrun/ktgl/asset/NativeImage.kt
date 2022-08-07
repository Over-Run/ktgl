package org.overrun.ktgl.asset

import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.overrun.ktgl.io.IFileProvider
import java.nio.ByteBuffer

/**
 * @author squid233
 * @since 0.1.0
 */
class NativeImage(
    val name: String,
    provider: IFileProvider
) : AutoCloseable {
    val width: Int
    val height: Int
    val buffer: ByteBuffer

    init {
        MemoryStack.stackPush().use {
            val pw = it.mallocInt(1)
            val ph = it.mallocInt(1)
            val pc = it.mallocInt(1)
            buffer = stbi_load_from_memory(
                provider.resourceToBuffer(name, 8192).getOrThrow(),
                pw, ph, pc, STBI_rgb_alpha
            ) ?: throw IllegalStateException("Failed to load native image '$name': ${stbi_failure_reason()}")
            width = pw[0]
            height = ph[0]
        }
    }

    override fun close() {
        MemoryUtil.memFree(buffer)
    }
}
