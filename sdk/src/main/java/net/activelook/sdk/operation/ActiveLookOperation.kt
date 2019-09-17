package net.activelook.sdk.operation

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toBase64
import net.activelook.sdk.widget.BitmapWidget

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

    class SetDebug(on: Boolean) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = if (on) {
            arrayOf(ActiveLookCommand.Debug(true))
        } else {
            arrayOf(
                ActiveLookCommand.Debug(false)
            )
        }
    }

    object Version : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Version
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

    class AddBitmap(private val bitmap: Bitmap) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.SaveBitmap(
                bitmap.width * bitmap.height,
                bitmap.width,
                bitmap.toBase64()
            )
        )
    }

    object ListBitmaps : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.ListBitmaps
        )
    }

    class AddScreen(private val screen: Screen, private val contentResolver: ContentResolver) :
        ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand>
            get() {
                var commands: Array<ActiveLookCommand> = arrayOf()

                val bitmapWidgets = screen.widgets.filterIsInstance<BitmapWidget>()
                if (bitmapWidgets.isNotEmpty()) {
                    for (bitmapWidget in bitmapWidgets) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            Uri.parse(bitmapWidget.source)
                        )
                        commands += ActiveLookCommand.SaveBitmap(
                            bitmap.byteCount,
                            bitmap.width,
                            bitmap.toBase64()
                        )
                    }
                }

                commands += ActiveLookCommand.SaveLayout(screen)

                return commands
            }
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