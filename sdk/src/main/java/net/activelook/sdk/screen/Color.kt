package net.activelook.sdk.screen

class Color {

    val r: Int
    val g: Int
    val b: Int

    constructor(r: Int, g: Int, b: Int) {
        this.r = r
        this.g = g
        this.b = b
    }

    constructor(colorHex: String) {
        val color = parseColor(colorHex)
        r = getRed(color)
        g = getGreen(color)
        b = getBlue(color)
    }

    companion object {
        fun parseColor(colorString: String): Int {
            if (colorString[0] == '#') {
                // Use a long to avoid rollovers on #ffXXXXXX
                var color = colorString.substring(1).toLong(16)
                if (colorString.length == 7) {
                    // Set the alpha value
                    color = color or 0x00000000ff000000
                } else require(colorString.length == 9) { "Unknown color" }
                return color.toInt()
            }
            throw IllegalArgumentException("Unknown color")
        }

        fun getRed(color: Int): Int {
            return (color shr 16) and 0xFF
        }

        fun getGreen(color: Int): Int {
            return (color shr 8) and 0xFF
        }

        fun getBlue(color: Int): Int {
            return color and 0xFF
        }
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