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

    class Display(on: Boolean) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = if (on) {
            arrayOf(ActiveLookCommand.Power(true))
        } else {
            arrayOf(
                ActiveLookCommand.Power(false),
                ActiveLookCommand.Clear
            )
        }
    }

    object ClearScreen : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Clear
        )
    }

    object GetBattery: ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.BatteryLevel
        )
    }
}