package net.activelook.sdk.operation

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.util.Base64
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

        object Flow : Notify() {
            override val commands: Array<ActiveLookCommand> = arrayOf(
                ActiveLookCommand.Notify.Flow
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

    private class AddBitmap(private val bitmap: Bitmap) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand>
            get() {
                val grayByteArray = toGrayByteArray(bitmap)
                val dataList = toBase64(grayByteArray).split("\n")

                var commands = arrayOf<ActiveLookCommand>(
                    ActiveLookCommand.SaveBitmap(
                        grayByteArray.size / 2,
                        bitmap.width
                    )
                )

                for (data in dataList) {
                    if (data.isEmpty()) {
                        continue
                    }
                    commands += ActiveLookCommand.SaveBitmapData(data)
                }

                return commands
            }
    }

    object ListBitmaps : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.ListBitmaps
        )
    }

    class AddScreen(private val screen: Screen) : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand>
            get() {
                var commands: Array<ActiveLookCommand> = arrayOf()

                val layout = screen.mapToLayout(screen.id, 10)

                commands += ActiveLookCommand.SaveLayout(layout)

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

    internal fun toGrayByteArray(bitmap: Bitmap): ByteArray {
        val grayList = mutableListOf<Byte>()
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.red(pixel)
                val blue = Color.red(pixel)
                val color = net.activelook.sdk.screen.Color(red, green, blue)
                val gray = color.getGrayscale()
                grayList += gray.toByte()
            }
        }

        return grayList.toByteArray()
    }

    internal fun toBase64(grayByteArray: ByteArray): String {
        val chunked = Base64.encodeToString(grayByteArray, Base64.DEFAULT)
        val encoded = chunked.filter { it != '\r' }
        return encoded
    }
}