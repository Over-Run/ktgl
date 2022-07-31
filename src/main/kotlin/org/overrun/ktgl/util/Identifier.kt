package org.overrun.ktgl.util

/**
 * @author squid233
 * @since 0.1.0
 */
class Identifier private constructor(identifier: List<String>) {
    companion object {
        const val DEFAULT_NAMESPACE = "ktgl"

        @JvmStatic
        fun toFile(type: ResourceType, identifier: String): String =
            identifier.split(':', limit = 2).let {
                if (it.size > 1) "$type/${it[0]}/${it[1]}"
                "$type/$DEFAULT_NAMESPACE/${it[0]}"
            }
    }

    val namespace: String
    val path: String

    init {
        if (identifier.size > 1) {
            namespace = identifier[0]
            path = identifier[1]
        } else {
            namespace = DEFAULT_NAMESPACE
            path = identifier[0]
        }
    }

    constructor(identifier: String) : this(identifier.split(':', limit = 2))
    constructor(namespace: String, path: String) : this("$namespace:$path")

    fun toFile(type: ResourceType): String = "$type/$namespace/$path"

    override fun toString(): String = "$namespace:$path"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Identifier

        if (namespace != other.namespace) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}
