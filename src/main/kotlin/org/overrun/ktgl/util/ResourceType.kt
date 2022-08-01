package org.overrun.ktgl.util

/**
 * @author squid233
 * @since 0.1.0
 */
enum class ResourceType {
    ASSETS;

    infix fun toFile(identifier: String) = identifier toFile this

    override fun toString(): String {
        return name.lowercase()
    }
}
