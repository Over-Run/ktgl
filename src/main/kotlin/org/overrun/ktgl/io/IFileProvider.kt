package org.overrun.ktgl.io

import org.lwjgl.BufferUtils
import java.io.*
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


/**
 * @author squid233
 * @since 0.1.0
 */
fun interface IFileProvider {
    fun getUrl(name: String): URL?
    fun toStream(name: String): Result<InputStream> {
        val url = getUrl(name)
        return url?.run {
            try {
                Result.success(openStream())
            } catch (e: IOException) {
                Result.failure(e)
            }
        } ?: Result.failure(IllegalStateException("getUrl(name) is null! name: $name"))
    }

    fun resourceToBuffer(resource: String, bufferSize: Int): Result<ByteBuffer> {
        val url = getUrl(resource) ?: return Result.failure(IOException("Resource not found: $resource"))
        val file = File(url.file)
        if (file.isFile) {
            return Result.success(FileInputStream(file).use {
                it.channel.use { fc ->
                    fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
                }
            })
        }
        var buffer = BufferUtils.createByteBuffer(bufferSize)
        val source = url.openStream() ?: return Result.failure(FileNotFoundException(resource))
        return source.use {
            val buf = ByteArray(8192)
            while (true) {
                val bytes = it.read(buf)
                if (bytes == -1) break
                if (buffer.remaining() < bytes)
                    buffer = BufferUtils.createByteBuffer(
                        maxOf(buffer.capacity() * 2, buffer.capacity() - buffer.remaining() + bytes)
                    ).apply {
                        put(buffer.flip())
                    }
                buffer.put(buf, 0, bytes)
            }
            Result.success(buffer.flip())
        }
    }

    private fun Reader.buildLines(): String = buildString { this@buildLines.forEachLine(::appendLine) }

    fun useLines(name: String): String = toStream(name).getOrThrow().bufferedReader().buildLines()

    fun useLinesOrNull(name: String): String? =
        toStream(name).getOrNull()?.bufferedReader()?.buildLines()

    fun useLines(name: String, block: (String) -> Unit) {
        toStream(name).getOrThrow().bufferedReader().forEachLine(block)
    }

    fun useLinesOrNull(name: String, block: (String) -> Unit) {
        toStream(name).getOrNull()?.bufferedReader()?.forEachLine(block)
    }

    companion object {
        @JvmField
        val LOCAL = IFileProvider { name -> File(name).toURI().toURL() }

        @JvmField
        val SYSTEM = IFileProvider(ClassLoader::getSystemResource)

        @JvmStatic
        fun ofCaller(): IFileProvider =
            ofClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass)

        @JvmStatic
        fun ofClass(clazz: Class<*>): IFileProvider = ofClassLoader(clazz.classLoader)

        @JvmStatic
        fun ofClassLoader(classLoader: ClassLoader) = IFileProvider(classLoader::getResource)
    }
}
