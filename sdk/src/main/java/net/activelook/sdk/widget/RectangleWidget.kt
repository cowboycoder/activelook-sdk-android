package net.activelook.sdk.widget

import net.activelook.sdk.screen.Color
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex

data class RectangleWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val isFilled: Boolean = true,
    val color: Color? = null
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

    override val command: String
        get() {
            var command = ""
            if (color != null) {
                command += "${ID_COLOR.toHex()}${color.getGrayscale().toHex()}"
            }

            val x0 = Screen.MAX_WIDTH - this.paddingLeft - this.x
            val y0 = Screen.MAX_HEIGHT - this.paddingTop - this.y
            val x1 = x0 - this.width
            val y1 = y0 - this.height

            val rectangleId = if (isFilled) {
                ID_RECTANGLE_FILLED
            } else {
                ID_RECTANGLE_OUTLINE
            }

            command += "${rectangleId.toHex()}${x0.toHex(4)}${y0.toHex(4)}" +
                    "${x1.toHex(4)}${y1.toHex(4)}"

            return command
        }

}

