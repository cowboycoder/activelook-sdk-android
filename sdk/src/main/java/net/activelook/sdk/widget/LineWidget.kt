package net.activelook.sdk.widget

import net.activelook.sdk.screen.Color
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex

data class LineWidget(
    val x0: Int,
    val y0: Int,
    val x1: Int,
    val y1: Int,
    val color: Color? = null
) : Widget(), HasPosition {

    override val x: Int = x0
    override val y: Int = y0

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

            val x0 = Screen.MAX_WIDTH - this.paddingLeft - this.x0
            val y0 = Screen.MAX_HEIGHT - this.paddingTop - this.y0
            val x1 = Screen.MAX_WIDTH - this.paddingLeft - this.x1
            val y1 = Screen.MAX_HEIGHT - this.paddingTop - this.y1

            command += "${ID_LINE.toHex()}${x0.toHex(4)}${y0.toHex(4)}${x1.toHex(4)}${y1.toHex(4)}"

            return command
        }

}

