package net.activelook.sdk.screen

import android.graphics.Color as AndroidColor

class Color(colorHex: String) {
    val r: Int
    val g: Int
    val b: Int

    init {
        val color = AndroidColor.parseColor(colorHex)
        r = AndroidColor.red(color)
        g = AndroidColor.green(color)
        b = AndroidColor.blue(color)
    }

    fun getGrayscale(): Int {
        // https://en.wikipedia.org/wiki/Grayscale#Converting_color_to_grayscale
        val linear = 0.2126f * r + 0.7152f * g + 0.0722f * b

        return (linear / 17).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Color) return false

        if (r != other.r) return false
        if (g != other.g) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = r
        result = 31 * result + g
        result = 31 * result + b
        return result
    }


}