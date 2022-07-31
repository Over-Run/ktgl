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
interface IFileProvider {
    fun getUrl(name: String): URL?
    fun toStream(name: String): InputStream? = getUrl(name)?.run {
        try {
            openStream()
        } catch (e: IOException) {
            null
        }
    }

    private fun Reader.buildLines(): String = StringBuilder().also { forEachLine(it::appendLine) }.toString()

    fun useLines(name: String): String =
        toStream(name)!!.bufferedReader().buildLines()

    fun useLinesOrNull(name: String): String? =
        toStream(name)?.bufferedReader()?.buildLines()

    companion object {
        @JvmField
        val LOCAL = object : IFileProvider {
            override fun getUrl(name: String): URL = File(name).toURI().toURL()
            override fun toStream(name: String): InputStream = getUrl(name).openStream()
        }

        @JvmStatic
        fun ofCaller(): IFileProvider =
            ofClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass)

        @JvmStatic
        fun ofClass(clazz: Class<*>): IFileProvider = ofClassLoader(clazz.classLoader)

        @JvmStatic
        fun ofClassLoader(classLoader: ClassLoader): IFileProvider = object : IFileProvider {
            override fun getUrl(name: String): URL? = classLoader.getResource(name)
        }
    }
}
