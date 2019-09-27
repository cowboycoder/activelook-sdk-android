package net.activelook.sdk.widget

import net.activelook.sdk.Font
import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

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

    override fun mapToLayoutWidget(): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        if (font != null) {
            widgets += LayoutWidget.Font(font.value)
        }

        widgets += LayoutWidget.Text(x, y, text)

        return widgets
    }

}

