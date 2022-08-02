package org.overrun.ktgl.io

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.net.URL

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

    private fun Reader.buildLines(): String = buildString { this@buildLines.forEachLine(::appendLine) }

    fun useLines(name: String): String = toStream(name).getOrThrow().bufferedReader().buildLines()

    fun useLinesOrNull(name: String): String? =
        toStream(name).getOrNull()?.bufferedReader()?.buildLines()

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
