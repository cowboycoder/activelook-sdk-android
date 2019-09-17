package net.activelook.sdk.screen

abstract class Widget {

    internal abstract val id: Int

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