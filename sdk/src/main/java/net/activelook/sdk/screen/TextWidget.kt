package net.activelook.sdk.screen

class TextWidget(
    val x: Int,
    val y: Int,
    val orientation: Orientation,
    val color: String,
    val font: String,
    val size: Int
) : Widget {
    override val type: String = "text"
}