package net.activelook.sdk.screen

import net.activelook.sdk.util.toHex

class TextWidget(
    val x: Int,
    val y: Int,
    val text: String
) : Widget("text") {

    override val id: Int = 9

    override val command: String = "${id.toHex()}${x.toHex(4)}${y.toHex(4)}" +
            "${text.length.toHex()}${text.toHex()}"

}