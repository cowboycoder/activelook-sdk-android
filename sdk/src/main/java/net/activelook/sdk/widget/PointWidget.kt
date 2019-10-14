package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

/**
 * Add a point to the [net.activelook.sdk.screen.Screen]
 */
data class PointWidget(
    override val x: Int,
    override val y: Int,
    val color: Color? = null
) : Widget(), HasPosition {

    override fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        widgets += LayoutWidget.Point(x, y)

        return widgets
    }
}

