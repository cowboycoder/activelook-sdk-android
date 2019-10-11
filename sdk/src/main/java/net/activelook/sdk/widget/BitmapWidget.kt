package net.activelook.sdk.widget

import net.activelook.sdk.layout.LayoutWidget

data class BitmapWidget(
    override val x: Int,
    override val y: Int,
    val height: Int,
    val width: Int,
    val sources: List<BitmapSource>,
    val default: String,
    val grayLevel: Int
) : Widget(), HasPosition {

    class BitmapSource(val id: String, val path: String)

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

    private var startBitmapId = -1

    internal fun setStartBitmapId(bitmapId: Int) {
        startBitmapId = bitmapId
    }

    fun setActiveBitmap(id: String) {
        val bitmapId = sources.find { it.id == id }?.id
    }

    override fun mapToLayoutWidget(): List<LayoutWidget> {
        val widgets = mutableListOf<LayoutWidget>()

        var id = startBitmapId

        for (source in sources) {
            widgets += LayoutWidget.Bitmap(id++, x, y)
        }

        return widgets
    }

}