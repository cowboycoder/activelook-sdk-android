package net.activelook.sdk.util

import android.graphics.*
import android.util.Base64
import kotlin.math.roundToInt


/**
 * First attempt for grayscale conversion with 15 levels
 * TODO: need accepted format (right now using ARGB_4444)
 * TODO: this is extremely costly, need to optimize / move to background thread
 */
fun Bitmap.toGrayscale(level: Int): Bitmap {

    val grayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
    val c = Canvas(grayScale)
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(
            ColorMatrix().apply { setSaturation(0f) }
        )
    }
    c.drawBitmap(this, 0f, 0f, paint)

    val pixels = IntArray(width * height)
    grayScale.getPixels(pixels, 0, width, 0, 0, width, height)

    val clampedLevel = Math.max(1, Math.min(level, 16))

    val mutableBitmap = grayScale.copy(Bitmap.Config.RGB_565, true)
    for(x in 0 until width) {
        for(y in 0 until height) {
            val pixel = grayScale.getPixel(x, y)
            val blue = Color.blue(pixel).toFloat()

            val normalizedValue = ((((blue - 0f) * (clampedLevel - 0f)) / (255f - 0f)) + 0f).roundToInt()
            val projectedValue = (((normalizedValue - 0) * (255 - 0)) / (clampedLevel - 0)) + 0
            val gray = Color.rgb(projectedValue, projectedValue, projectedValue)
            mutableBitmap.setPixel(x, y, gray)
        }
    }

    return mutableBitmap
}

fun Bitmap.toBase64(): String {
    val grayBitmap = mutableListOf<Byte>()
    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = this.getPixel(x, y)
            val red = Color.red(pixel)
            val green = Color.red(pixel)
            val blue = Color.red(pixel)
            val color = net.activelook.sdk.screen.Color(red, green, blue)
            val gray = color.getGrayscale()
            grayBitmap += gray.toByte()
        }
    }
    val byteArray = grayBitmap.toByteArray()
    val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
    return encoded
}