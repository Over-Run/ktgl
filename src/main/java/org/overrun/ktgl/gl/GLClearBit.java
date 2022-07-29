package org.overrun.ktgl.gl;

import static org.lwjgl.opengl.GL11C.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public enum GLClearBit {
    NONE(0),
    COLOR(GL_COLOR_BUFFER_BIT),
    DEPTH(GL_DEPTH_BUFFER_BIT),
    STENCIL(GL_STENCIL_BUFFER_BIT),
    COLOR_DEPTH(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT),
    COLOR_STENCIL(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT),
    DEPTH_STENCIL(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT),
    ALL(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

    private final int bits;

    GLClearBit(int bits) {
        this.bits = bits;
    }

    /**
     * Get bits.
     *
     * @return bits
     */
    public int getBits() {
        return bits;
    }
}
