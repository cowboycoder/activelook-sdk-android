package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.util.toHex

data class TextWidget(
    override val x: Int,
    override val y: Int,
    val text: String,
    val font: Font? = null,
    val color: Color? = null
) : Widget(), HasPosition {

    override val id: Int = 9

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
                command += "${3.toHex()}${color.getGrayscale().toHex()}"
            }

            if (font != null) {
                command += "${4.toHex()}${font.value.toHex()}"
            }

            val x = Screen.MAX_WIDTH - this.paddingLeft - this.x
            val y = Screen.MAX_HEIGHT - this.paddingTop - this.y

            command += "${id.toHex()}${x.toHex(4)}${y.toHex(4)}" +
                    "${text.length.toHex()}${text.toHex()}"

            return command
        }

}

