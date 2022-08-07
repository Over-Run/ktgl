package org.overrun.ktgl.util

import kotlin.math.abs

/**
 * Converts an RGB color value to HSL. Conversion formula adapted from
 * [https://stackoverflow.com/a/54071699](https://stackoverflow.com/a/54071699).
 * Assumes r, g, and b are contained in the set &#91;0, 1&#93; and
 * returns h in the set &#91;0, 360&#93;, s and l in the set &#91;0, 1&#93;.
 *
 * @param[r] The red color value
 * @param[g] The green color value
 * @param[b] The blue color value
 * @return The HSL representation
 */
fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
    val v = maxOf(r, g, b)
    val c = v - minOf(r, g, b)
    val f = 1f - abs(v + v - c - 1f)
    val h = if (c == 0f || c.isNaN()) c
    else if (v == r) (g - b) / c
    else if (v == g) 2f + (b - r) / c
    else 4f + (r - g) / c
    return floatArrayOf(
        60f * (if (h < 0f) h + 6f else h),
        if (f == 0f || f.isNaN()) c / f else 0f,
        (v + v - c) * 0.5f
    )
}

/**
 * Converts an HSL color value to RGB. Conversion formula adapted from
 * [https://stackoverflow.com/a/64090995](https://stackoverflow.com/a/64090995).
 * Assumes h is contained in the set &#91;0, 360&#93;, s and l are contained
 * in the set &#91;0, 1&#93; and returns r, g, and b in the set &#91;0, 1&#93;.
 *
 * @param[h] The hue
 * @param[s] The saturation
 * @param[l] The lightness
 * @return The RGB representation
 */
fun hslToRgb(h: Float, s: Float, l: Float): FloatArray {
    val a = s * minOf(l, 1f - l)
    val f = fun(n: Int): Float = ((n + h / 30f) % 12).let { k -> l - a * maxOf(minOf(k - 3f, 9f - k, 1f), -1f) }
    return floatArrayOf(f(0), f(8), f(4))
}

/**
 * Converts an RGB color value to HSV. Conversion formula adapted from
 * [https://stackoverflow.com/a/54070620](https://stackoverflow.com/a/54070620).
 * Assumes r, g, and b are contained in the set &#91;0, 1&#93; and
 * returns h in the set &#91;0, 360&#93;, s and v in the set &#91;0, 1&#93;.
 *
 * @param[r] The red color value
 * @param[g] The green color value
 * @param[b] The blue color value
 * @return The HSV representation
 */
fun rgbToHsv(r: Float, g: Float, b: Float): FloatArray {
    val v = maxOf(r, g, b)
    val c = v - minOf(r, g, b)
    val h = if (c == 0f || c.isNaN()) c
    else if (v == r) (g - b) / c
    else if (v == g) 2f + (b - r) / c
    else 4f + (r - g) / c
    return floatArrayOf(
        60f * (if (h < 0f) h + 6f else h),
        if (v == 0f || v.isNaN()) v else c / v,
        v
    )
}

/**
 * Converts an HSV color value to RGB. Conversion formula adapted from
 * [https://stackoverflow.com/a/54024653](https://stackoverflow.com/a/54024653).
 * Assumes h is contained in the set &#91;0, 360&#93;, s and v are contained
 * in the set &#91;0, 1&#93; and returns r, g, and b in the set &#91;0, 1&#93;.
 *
 * @param[h] The hue
 * @param[s] The saturation
 * @param[v] The value
 * @return The RGB representation
 */
fun hsvToRgb(h: Float, s: Float, v: Float): FloatArray {
    val f = fun(n: Int): Float = ((n + h / 60f) % 6).let { k -> v - v * s * maxOf(minOf(k, 4f - k, 1f), 0f) }
    return floatArrayOf(f(5), f(3), f(1))
}

fun hslToHsv(h: Float, s: Float, l: Float): FloatArray =
    (s * minOf(l, 1f + l) + l).let { v ->
        floatArrayOf(h, if (v == 0f || v.isNaN()) 0f else (2f - 2f * l / v), v)
    }

fun hsvToHsl(h: Float, s: Float, v: Float): FloatArray =
    (v - v * s * 0.5f).let { l ->
        minOf(1f, 1f - l).let { m ->
            floatArrayOf(h, if (m == 0f || m.isNaN()) 0f else (v - l) / m, l)
        }
    }

