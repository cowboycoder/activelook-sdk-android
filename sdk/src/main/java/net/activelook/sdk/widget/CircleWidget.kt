package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.screen.Color

/**
 * Add a circle to the [net.activelook.sdk.screen.Screen]
 */
data class CircleWidget(
    override val x: Int,
    override val y: Int,
    val radius: Int,
    val isFilled: Boolean = true,
    val color: Color? = null
) : Widget(), HasPosition {

    override fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        if (color != null) {
            widgets += LayoutWidget.Font(color.getGrayscale())
        }

        val x = paddingLeft + x
        val y = paddingTop + y

        widgets += LayoutWidget.Circle(isFilled, x, y, radius)

        return widgets
    }
}

