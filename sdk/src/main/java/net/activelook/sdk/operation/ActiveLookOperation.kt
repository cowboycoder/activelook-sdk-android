package net.activelook.sdk.operation

import android.graphics.Point
import android.graphics.Rect
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.screen.Screen

sealed class ActiveLookOperation {

    internal abstract val commands: Array<ActiveLookCommand>

    sealed class Notify: ActiveLookOperation() {

        object BatteryLevel: Notify() {
            override val commands: Array<ActiveLookCommand> = arrayOf(
                ActiveLookCommand.Notify.BatteryLevel
            )
        }

        object TxServer: Notify() {
            override val commands: Array<ActiveLookCommand> = arrayOf(
                ActiveLookCommand.Notify.TxServer
            )
        }
    }

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

    class SetLed(on: Boolean) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = if (on) {
            arrayOf(ActiveLookCommand.Led(true))
        } else {
            arrayOf(
                ActiveLookCommand.Led(false)
            )
        }
    }

    class SetBrightness(level: Int, autoAdjust: Boolean) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.AmbientLightSensor(autoAdjust),
            ActiveLookCommand.Luminosity(level)
        )
    }

    object GetBattery: ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.BatteryLevel
        )
    }

    class AddScreen(screen: Screen) : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.SaveLayout(screen.mapToCommand())
        )
    }

    class DeleteScreen(screenId: Int) : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.EraseLayout(screenId)
        )
    }

    class DeleteAllScreens : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand>
            get() {
                return IntRange(Screen.ID_MIN, Screen.ID_MAX)
                    .map {
                        ActiveLookCommand.EraseLayout(it)
                    }
                    .toTypedArray()
            }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun hashCode(): Int {
            return System.identityHashCode(this)
        }
    }

    class DisplayScreen(screenId: Int, text: String) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.DisplayLayout(screenId, text)
        )
    }
}