package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

/**
 * Add a line to the [net.activelook.sdk.screen.Screen].
 */
data class LineWidget(
    val x0: Int,
    val y0: Int,
    val x1: Int,
    val y1: Int,
    val color: Color? = null
) : Widget(), HasPosition {

    override val x: Int = x0
    override val y: Int = y0

    override fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        val x0 = paddingLeft + x
        val y0 = paddingTop + y
        val x1 = paddingLeft + this.x1
        val y1 = paddingTop + this.y1

        widgets += LayoutWidget.Line(x0, y0, x1, y1)

        return widgets
    }
}

