package net.activelook.sdk.widget

import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex

data class BitmapWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val source: String,
    val grayLevel: Int
) : Widget(), HasPosition {

    override var paddingLeft: Int = 0
    override var paddingTop: Int = 0
    override var paddingRight: Int = 0
    override var paddingBottom: Int = 0

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        paddingLeft = left
        paddingTop = top
        paddingRight = right
        paddingBottom = bottom
    }

    internal fun setBitmapId(bitmapId: Int) {

    }

    override val command: String
        get() {
            val x0 = Screen.MAX_WIDTH - this.paddingLeft - this.x
            val y0 = Screen.MAX_HEIGHT - this.paddingTop - this.y

            val bitmapId = -1

            return "${ID_BITMAP.toHex()}${bitmapId.toHex()}${x0.toHex(4)}${y0.toHex(4)}"
        }

}