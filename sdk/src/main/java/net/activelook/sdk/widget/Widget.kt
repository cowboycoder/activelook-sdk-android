package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget

/**
 * A widget is graphical component of a [net.activelook.sdk.screen.Screen].
 */
abstract class Widget {

    internal abstract fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget>

}

interface HasPosition {
    val x: Int
    val y: Int
}