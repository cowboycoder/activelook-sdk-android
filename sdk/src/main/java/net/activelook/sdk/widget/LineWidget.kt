package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

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

    override fun mapToLayoutWidget(): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        widgets += LayoutWidget.Line(x, y, x1, y1)

        return widgets
    }
}

