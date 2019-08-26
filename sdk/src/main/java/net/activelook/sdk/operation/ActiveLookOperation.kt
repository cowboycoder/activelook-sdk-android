package net.activelook.sdk.operation

import android.graphics.Point
import android.graphics.Rect
import net.activelook.sdk.command.ActiveLookCommand

internal sealed class ActiveLookOperation {

    abstract val commands: Array<ActiveLookCommand>

    object Hello: ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Clear,
            ActiveLookCommand.Text("Hello, World", Point(264, 216), 4, 2, 15),
            ActiveLookCommand.Rectangle(Rect(0, 0, 304, 256), false)
//            ActiveLookCommand.Write.Rectangle(Rect(264, 20, 100, 100), true)
        )
    }

    object GetBattery: ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.BatteryLevel
        )
    }
}