package org.overrun.ktgl.util

/**
 * @author squid233
 * @since 0.1.0
 */
enum class ResourceType {
    ASSETS;

    override fun toString(): String {
        return name.lowercase()
    }
}