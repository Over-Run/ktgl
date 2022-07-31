package org.overrun.ktgl.scene

/**
 * @author squid233
 * @since 0.1.0
 */
class GameObjectImpl : GameObject<GameObjectImpl>() {
    override fun getThis(): GameObjectImpl = this
}
