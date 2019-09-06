package net.activelook.sdk.screen

abstract class Widget(val type: String) {

    internal abstract val id: Int

    internal abstract val command: String

    internal fun getCommandSize(): Int {
        return command.length / 2
    }

}