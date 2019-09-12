package net.activelook.sdk.widget

abstract class Widget {

    protected companion object {
        const val ID_BITMAP = 0
        const val ID_CIRCLE_OUTLINE = 1
        const val ID_CIRCLE_FILLED = 2
        const val ID_COLOR = 3
        const val ID_FONT = 4
        const val ID_LINE = 5
        const val ID_POINT = 6
        const val ID_RECTANGLE_OUTLINE = 7
        const val ID_RECTANGLE_FILLED = 8
        const val ID_TEXT = 9
    }

    internal abstract val command: String

    internal fun getCommandSize(): Int {
        return command.length / 2
    }

}

interface HasPosition {
    val x: Int
    val y: Int

    var paddingLeft: Int
    var paddingTop: Int
    var paddingRight: Int
    var paddingBottom: Int

    fun setPadding(
        left: Int = paddingLeft,
        top: Int = paddingTop,
        right: Int = paddingRight,
        bottom: Int = paddingBottom
    )
}