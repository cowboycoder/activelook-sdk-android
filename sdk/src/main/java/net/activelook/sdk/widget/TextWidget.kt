package net.activelook.sdk.widget

import net.activelook.sdk.Font
import net.activelook.sdk.screen.Color
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex

data class TextWidget(
    override val x: Int,
    override val y: Int,
    val text: String,
    val font: Font? = null,
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

            if (font != null) {
                command += "${ID_FONT.toHex()}${font.value.toHex()}"
            }

            val x = Screen.MAX_WIDTH - this.paddingLeft - this.x
            val y = Screen.MAX_HEIGHT - this.paddingTop - this.y

            command += "${ID_TEXT.toHex()}${x.toHex(4)}${y.toHex(4)}" +
                    "${text.length.toHex()}${text.toHex()}"

            return command
        }

}

