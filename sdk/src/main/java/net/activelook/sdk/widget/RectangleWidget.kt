package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

data class RectangleWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val isFilled: Boolean = true,
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

        val x1 = x - this.width
        val y1 = y - this.height

        widgets += LayoutWidget.Rectangle(isFilled, x, y, x1, y1)

        return widgets
    }

}

