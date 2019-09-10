package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.util.toHex

data class TextWidget(
    val x: Int,
    val y: Int,
    val text: String,
    val font: Font? = null,
    val color: Color? = null
) : Widget() {

    override val id: Int = 9

    override val command: String
        get() {
            var command = ""
            if (color != null) {
                command += "${3.toHex()}${color.getGrayscale().toHex()}"
            }

            if (font != null) {
                command += "${4.toHex()}${font.value.toHex()}"
            }

            command += "${id.toHex()}${x.toHex(4)}${y.toHex(4)}" +
                    "${text.length.toHex()}${text.toHex()}"

            return command
        }

}

