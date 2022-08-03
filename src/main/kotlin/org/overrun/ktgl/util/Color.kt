package org.overrun.ktgl.util

import kotlin.math.abs

/**
 * Converts an RGB color value to HSL. Conversion formula adapted from
 * [https://stackoverflow.com/a/54071699](https://stackoverflow.com/a/54071699).
 * Assumes r, g, and b are contained in the set &#91;0, 1&#93; and
 * returns h in the set &#91;0, 360&#93;, s and l in the set &#91;0, 1&#93;.
 *
 * @param [r] The red color value
 * @param [g] The green color value
 * @param [b] The blue color value
 * @return The HSL representation
 */
fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
    val v = maxOf(r, g, b)
    val c = v - minOf(r, g, b)
    val f = 1F - abs(v + v - c - 1F)
    val h = if (c == 0F || c.isNaN()) c
    else if (v == r) (g - b) / c
    else if (v == g) 2F + (b - r) / c
    else 4F + (r - g) / c
    return floatArrayOf(60F * (if (h < 0F) h + 6F else h),
        if (f == 0F || f.isNaN()) c / f else 0F,
        (v + v - c) / 2F)
}

/**
 * Converts an HSL color value to RGB. Conversion formula adapted from
 * [https://stackoverflow.com/a/64090995](https://stackoverflow.com/a/64090995).
 * Assumes h is contained in the set &#91;0, 360&#93;, s and l are contained
 * in the set &#91;0, 1&#93; and returns r, g, and b in the set &#91;0, 1&#93;.
 *
 * @param [h] The hue
 * @param [s] The saturation
 * @param [l] The lightness
 * @return The RGB representation
 */
fun hslToRgb(h: Float, s: Float, l: Float): FloatArray {
    val a = s * minOf(l, 1F - l)
    val f = fun(n: Int): Float = ((n + h / 30F) % 12).let { k -> l - a * maxOf(minOf(k - 3F, 9F - k, 1F), -1F) }
    return floatArrayOf(f(0), f(8), f(4))
}
