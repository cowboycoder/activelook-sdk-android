package net.activelook.sdk.widget

import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex

data class BitmapWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val sources: List<BitmapSource>,
    val grayLevel: Int
) : Widget(), HasPosition {

    class BitmapSource(val id: String, val path: String)

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

    fun setActiveBitmap(id: String) {
        val bitmapId = sources.find { it.id == id }?.id
    }

    override val command: String
        get() {
            val x0 = Screen.MAX_WIDTH - this.paddingLeft - this.x
            val y0 = Screen.MAX_HEIGHT - this.paddingTop - this.y

            val bitmapId = -1



            return "${ID_BITMAP.toHex()}${bitmapId.toHex()}${x0.toHex(4)}${y0.toHex(4)}"
        }

}