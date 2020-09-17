package net.activelook.sdk.operation

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Base64
import net.activelook.sdk.Font
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.Point

private val screens = mutableListOf<Screen>()
private val dynamicTexts = mutableMapOf<Int, String>()

sealed class ActiveLookOperation {

    internal abstract val commands: Array<ActiveLookCommand>

    internal sealed class Notify : ActiveLookOperation() {

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

    /**
     * Clear the screen then draw a text and a rectangle.
     */
    object Hello: ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Clear,
            ActiveLookCommand.Text("Hello, World", Point(264, 216), 4, 2, 15),
            ActiveLookCommand.Rectangle(Rect(0, 0, 301, 255), false)
//            ActiveLookCommand.Write.Rectangle(Rect(264, 20, 100, 100), true)
        )
    }

    class DrawText(private val text: String, private val at: Point, private val font: Font = Font.MEDIUM): ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand>
            get() {
                var commands = arrayOf<ActiveLookCommand>()
                commands += ActiveLookCommand.Text(text, at, 4, font.value, 15)
                return commands
            }
    }

    class DrawPath(private val path: ArrayList<Point>) : ActiveLookOperation() {

        internal override val commands: Array<ActiveLookCommand>
            get() {
                var commands = arrayOf<ActiveLookCommand>()

                var lastPoint : Point? = null
                path.forEach {
                    if(lastPoint != null) {
                        if(Screen.isOnScreen(lastPoint!!) || Screen.isOnScreen(it)) {
                            commands += ActiveLookCommand.Line(lastPoint!!, it)
                        }
                    }
                    lastPoint = it
                }

                return commands
            }
    }

    class DrawRect(private val rect: Rect) : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand>
            get() {
                return arrayOf(ActiveLookCommand.Rectangle(rect, false))
            }
    }

    class DrawCircle(private val centre: Point, private val radius: Int) : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand>
            get() {
                return arrayOf(ActiveLookCommand.Circle(centre, radius, false))
            }
    }

    class DrawBitmap(private val bmpId: Int, private val at: Point) : ActiveLookOperation() {

        override val commands: Array<ActiveLookCommand>
            get() {
                return arrayOf(ActiveLookCommand.Bitmap(bmpId, at.x, at.y))
            }
    }

    /**
     * Power on or off the screen.
     *
     * @param on if true, power on the screen, else, power off and clear
     */
    class Display(on: Boolean) : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = if (on) {
            arrayOf(ActiveLookCommand.Power(true))
        } else {
            arrayOf(
                ActiveLookCommand.Power(false),
                ActiveLookCommand.Clear
            )
        }
    }

    /**
     * Clear the screen.
     */
    object ClearScreen : ActiveLookOperation() {

        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Clear
        )
    }

    class FillScreen(color: Int): ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Grey(color)
        )
    }

    /**
     * Change the debug mode. If the debug mode is enabled, every command sent are returned by the device.
     *
     * @param on if true, enabled the debug mode, else, disabled it
     */
    internal class SetDebug(on: Boolean) : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = if (on) {
            arrayOf(ActiveLookCommand.Debug(true))
        } else {
            arrayOf(
                ActiveLookCommand.Debug(false)
            )
        }
    }

    /**
     * Get the version number.
     */
    internal object Version : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.Version
        )
    }

    /**
     * Active the LED or not.
     *
     * @param on if true, power on the LED, else, power off
     */
    class SetLed(on: Boolean) : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = if (on) {
            arrayOf(ActiveLookCommand.Led(true))
        } else {
            arrayOf(
                ActiveLookCommand.Led(false)
            )
        }
    }

    /**
     * Set the overall brightness and activate or not the ambient light sensor
     *
     * The ambient light sensor will change automatically the brightness when it is activated.
     *
     * @param level Level of brightness, must be between 0 and 15
     * @param autoAdjust Activate or not the ambient light sensor
     */
    class SetBrightness(level: Int, autoAdjust: Boolean) : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.AmbientLightSensor(autoAdjust),
            ActiveLookCommand.Luminosity(level)
        )
    }

    /**
     * Display the battery level on the screen
     */
    object GetBattery: ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.BatteryLevel
        )
    }

    class AddBitmap(private val bitmap: Bitmap) : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand>
            get() {
                val grayByteArray = toGrayByteArray(bitmap)
                val dataList = toBase64(grayByteArray).split("\n")

                var commands = arrayOf<ActiveLookCommand>(
                    ActiveLookCommand.SaveBitmap(
                        grayByteArray.size,
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

    class DisplayBitmap(val bmpId : Int, val x : Int, val y : Int) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand>
            get() { return arrayOf(ActiveLookCommand.Bitmap(bmpId, x, y)) }
    }

    /**
     * Get the list of bitmaps with their id and size
     */
    object ListBitmaps : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.ListBitmaps
        )
    }

    object ClearBitmaps : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand>
            get() = arrayOf(ActiveLookCommand.EraseAllBitmaps)
    }
    /**
     * Add a screen to the device
     *
     * @param screen The Screen to send
     */
    class AddScreen(private val screen: Screen) : ActiveLookOperation() {

        internal override val commands: Array<ActiveLookCommand>
            get() {
                var commands: Array<ActiveLookCommand> = arrayOf()

                val layout = screen.mapToLayout(screen.id, 10)

                screens += screen

                commands += ActiveLookCommand.SaveLayout(layout)

                return commands
            }
    }

    class RestoreScreen(private val layoutData: String) : ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand>
            get() {
                return arrayOf(ActiveLookCommand.SaveLayoutBytes(layoutData))
            }
    }

    /**
     * Delete a screen from the device
     *
     * @param screenId The id of the screen that will be deleted
     */
    class DeleteScreen(screenId: Int) : ActiveLookOperation() {

        internal override val commands: Array<ActiveLookCommand> = arrayOf(
            ActiveLookCommand.EraseLayout(screenId)
        )
    }

    /**
     * Delete all user screens from device
     */
    class DeleteAllScreens : ActiveLookOperation() {

        internal override val commands: Array<ActiveLookCommand>
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

    /**
     * Show a screen and if a variable text is defined, it will be displayed
     *
     * The device will be cleared before the screen will be shown.
     *
     * @param screenId The id of the screen that will be shown
     * @param text The text that will be displayed if a variable text is defined
     */
    class ShowScreen(private val screenId: Int, private val text: String = "") :
        ActiveLookOperation() {
        internal override val commands: Array<ActiveLookCommand>
            get() {
                var commands = arrayOf<ActiveLookCommand>()
                val screen = screens.find { it.id == screenId }
                val previousText = dynamicTexts[screenId]



                if (screen != null && !previousText.isNullOrEmpty() && previousText != text) {
                    val textPosition = screen.textPosition
                    val font = screen.font
                    val paint = Paint()
                    val bound = Rect()
                    paint.textSize = font.size.toFloat()
                    paint.getTextBounds(previousText, 0, previousText.length, bound)
                    val x0 = textPosition.x
                    val y0 = textPosition.y
                    val x1 = textPosition.x + bound.width()
                    val y1 = textPosition.y + bound.height()
                    commands += ActiveLookCommand.Color(0)
                    commands += ActiveLookCommand.Rectangle(x0, y0, x1, y1, true)
                }

                dynamicTexts[screenId] = text

                commands += ActiveLookCommand.DisplayLayout(screenId, text)



                return commands
            }
    }

    class CommandList(private val list: Array<ActiveLookCommand>) : ActiveLookOperation() {
        override val commands = list
    }

    class DrawListBuilder {

    }

    class ConfigureGauge(val gaugeNumber: Int, val x: Int, val y: Int, val radius: Int, val radiusInner: Int,
                         val start: Int, val end: Int, val clockwise: Boolean) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand>
            get() {
                return arrayOf(ActiveLookCommand.GaugeConfigure(gaugeNumber, x, y, radius, radiusInner, start, end, clockwise))
            }
    }

    class DisplayGauge(val gaugeNumber: Int, val value: Int) : ActiveLookOperation() {
        override val commands: Array<ActiveLookCommand>
            get() {
                return arrayOf(ActiveLookCommand.GaugeValue(gaugeNumber, value))
            }
    }

    internal fun toGrayByteArray(bitmap: Bitmap): ByteArray {
        val grayList = mutableListOf<Byte>()
        var gray : Int = 0
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.red(pixel)
                val blue = Color.red(pixel)
                val color = net.activelook.sdk.screen.Color(red, green, blue)
                if(x % 2 == 0) {
                    gray = color.getGrayscale().and(15) * 16
                } else {
                    gray.or(color.getGrayscale().and(15))
                    grayList += gray.toByte()
                }
            }
        }

        return grayList.toByteArray()
    }

    internal fun toBase64(grayByteArray: ByteArray): String {
        val chunked = Base64.encodeToString(grayByteArray, Base64.DEFAULT)
        val encoded = chunked.filter { it != '\r' }
        return encoded
    }

/*
    internal fun toGrayByteArray(bitmap: Bitmap): ByteArray {
        var line: String = ""
        val grayList = mutableListOf<Byte>()
        var gray: Int = 0
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.red(pixel)
                val blue = Color.red(pixel)
                if(x % 2 == 0) {
                    gray = (toGrayScale(red, green, blue) * 16)
                } else {
                    gray += (toGrayScale(red, green, blue))
                    line += gray.toHex(2)
                    grayList += gray.toByte()
                }
            }
            Log.d("BMP", "As Hex: $line")
            line = ""
        }

        return grayList.toByteArray()
    }

    private fun toGrayScale(r: Int, g: Int, b: Int): Int {
        val linear = 0.2126f * r + 0.7152f * g + 0.0722f * b
        return (linear / 17).toInt()
    }

    internal fun toBase64(grayByteArray: ByteArray): String {
        val chunked = Base64.encodeToString(grayByteArray, Base64.DEFAULT)
        val encoded = chunked.filter { it != '\r' }
        return encoded
    }
*/

}