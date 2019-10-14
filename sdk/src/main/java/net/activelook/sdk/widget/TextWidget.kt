package net.activelook.sdk.widget

import net.activelook.sdk.Font
import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

/**
 * Add a text to the [net.activelook.sdk.screen.Screen]
 */
data class TextWidget(
    override val x: Int,
    override val y: Int,
    val text: String,
    val font: Font? = null,
    val color: Color? = null
) : Widget(), HasPosition {

    override fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        if (font != null) {
            widgets += LayoutWidget.Font(font.value)
        }

        val x = paddingLeft + x
        val y = paddingTop + y

        widgets += LayoutWidget.Text(x, y, text)

        return widgets
    }

}

