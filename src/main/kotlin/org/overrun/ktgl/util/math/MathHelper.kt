package org.overrun.ktgl.util.math

import kotlin.math.PI

const val RAD60F = (60f * PI / 180f).toFloat()
const val RAD90F = (PI * 0.5f).toFloat()
const val RAD360F = (PI * 2f).toFloat()

/**
 * Convert float normal to byte.
 * @param[n] the normal value
 * @return the byte
 */
fun normalToByte(n: Float): Byte = ((255f * n - 1f) * .5f).toInt().toByte()
