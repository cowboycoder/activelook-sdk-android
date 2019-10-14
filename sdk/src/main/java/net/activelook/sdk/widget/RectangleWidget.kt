package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

/**
 * Add a rectangle to the [net.activelook.sdk.screen.Screen]
 */
data class RectangleWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val isFilled: Boolean = true,
    val color: Color? = null
) : Widget(), HasPosition {

    override fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        val x0 = paddingLeft + x
        val y0 = paddingTop + y
        val x1 = x0 + this.width
        val y1 = y0 + this.height

        widgets += LayoutWidget.Rectangle(isFilled, x0, y0, x1, y1)

        return widgets
    }

}

