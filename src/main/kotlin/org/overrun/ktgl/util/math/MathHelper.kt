package org.overrun.ktgl.util.math

/**
 * Convert float normal to byte.
 * @param[n] the normal value
 * @return the byte
 */
fun normalToByte(n: Float): Byte = ((255F * n - 1F) * .5F).toInt().toByte()
