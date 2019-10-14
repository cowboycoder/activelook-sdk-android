package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget

/**
 * Add a bitmap to the [net.activelook.sdk.screen.Screen]. The widget can have multiple source,
 * but only the active one will be display.
 */
data class BitmapWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val sources: List<BitmapSource>,
    /**
     * Default source, can be empty and no bitmap will be display
     */
    val default: String,
    /**
     * Level of gray, must be between 0 and 15.
     */
    val grayLevel: Int
) : Widget(), HasPosition {

    class BitmapSource(val id: String, val path: String)

    private var startBitmapId = -1

    internal fun setStartBitmapId(bitmapId: Int) {
        startBitmapId = bitmapId
    }

    fun setActiveBitmap(id: String) {
        val bitmapId = sources.find { it.id == id }?.id
    }

    override fun mapToLayoutWidget(paddingLeft: Int, paddingTop: Int): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        var id = startBitmapId

        val x = paddingLeft + x
        val y = paddingTop + y

        for (source in sources) {
            widgets += LayoutWidget.Bitmap(id++, x, y)
        }

        return widgets
    }

}