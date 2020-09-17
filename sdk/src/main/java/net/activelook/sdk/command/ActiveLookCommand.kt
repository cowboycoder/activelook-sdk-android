package net.activelook.sdk.command

import android.graphics.Rect
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.layout.Layout
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.Point
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

internal interface ActiveLookCommandWrapper {

    val commands: Array<ActiveLookCommand>

}

internal interface NeedPreviousResult {
    fun setPreviousResult(result: String)
}

internal interface NeedPreviousResults {
    fun setPreviousResults(results: List<String>)
}

internal interface NeedFastConnection

sealed class ActiveLookCommand {

    abstract val command: String

    // region System
    data class Power(val on: Boolean) : ActiveLookCommand() {
        override val command = if (on) {
            "power on"
        } else {
            "power off"
        }
    }

    object BatteryLevel: ActiveLookCommand() {
        override val command = "battery"
    }
    // endregion System

    // region Drawing

    // region Misc
    object Clear: ActiveLookCommand() {
        override val command = "clear"
    }

    data class Grey(val color: Int): ActiveLookCommand() {
        override val command = "grey $color"
    }

    data class Debug(val on: Boolean) : ActiveLookCommand() {
        override val command = if (on) {
            "debug on"
        } else {
            "debug off"
        }
    }

    object Version : ActiveLookCommand() {
        override val command = "vers"
    }

    data class Led(val on: Boolean) : ActiveLookCommand() {
        override val command = if (on) {
            "led on"
        } else {
            "led off"
        }
    }

    class Luminosity(level: Int) : ActiveLookCommand() {

        private val minLevel = 0
        private val maxLevel = 15

        val level = max(min(level, maxLevel), minLevel)

        override val command = "luma ${this.level}"


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Luminosity) return false

            if (level != other.level) return false
            if (command != other.command) return false

            return true
        }

        override fun hashCode(): Int {
            var result = minLevel
            result = 31 * result + maxLevel
            result = 31 * result + level
            result = 31 * result + command.hashCode()
            return result
        }
    }

    data class AmbientLightSensor(val on: Boolean) : ActiveLookCommand() {
        override val command = if (on) {
            "als on"
        } else {
            "als off"
        }
    }

    class Color(level: Int): ActiveLookCommand() {
        override val command = "color $level"
    }
    // endregion Misc

    // region Shapes
    class Rectangle(private val rect: Rect, private val filled: Boolean): ActiveLookCommand() {

        constructor(x0: Int, y0: Int, x1: Int, y1: Int, filled: Boolean) : this(
            Rect(
                x0,
                y0,
                x1,
                y1
            ), filled
        )

        override val command: String
            get() {
                val x0 = Screen.MAX_WIDTH - rect.left
                val y0 = Screen.MAX_HEIGHT - rect.top
                val x1 = Screen.MAX_WIDTH - rect.right
                val y1 = Screen.MAX_HEIGHT - rect.bottom
                if(filled) {
                    return "rectf $x0 $y0 $x1 $y1"
                } else {
                    return "rect $x0 $y0 $x1 $y1"
                }
            }
    }

    class Circle(private val centre: Point, private val radius: Int, private val filled: Boolean): ActiveLookCommand() {

        constructor(x: Int, y: Int, r: Int, filled: Boolean) : this(
                Point(x, y), r, filled
        )

        override val command: String
            get() {
                val x = Screen.MAX_WIDTH - centre.x
                val y = Screen.MAX_HEIGHT - centre.y
                if(filled) {
                    return "circf $x $y $radius"
                } else {
                    return "circ $x $y $radius"
                }
            }
    }

    class Line(private val from: Point, private val to: Point): ActiveLookCommand() {

        constructor(x0: Int, y0: Int, x1: Int, y1: Int) : this(Point(x0, y0), Point(x1, y1))

        override val command: String
            get() {
                val x0 = Screen.MAX_WIDTH - from.x
                val y0 = Screen.MAX_HEIGHT - from.y
                val x1 = Screen.MAX_WIDTH - to.x
                val y1 = Screen.MAX_HEIGHT - to.y
                return "line $x0 $y0 $x1 $y1"
            }
    }

    // endregion Shapes

    // region Text
    class Text(text: String, origin: Point, rotation: Int, fontSize: Int, color: Int): ActiveLookCommand() {
        // Currently the X/Y position needs adjusting due to physical screen orientation
        override val command = "txt ${Screen.MAX_WIDTH - origin.x} ${Screen.MAX_HEIGHT - origin.y} $rotation $fontSize $color $text"
    }
    // endregion Text

    // region Image

    object ListBitmaps : ActiveLookCommand() {
        override val command: String = "bmplist"
    }

    data class SaveBitmap(val size: Int, val width: Int) : ActiveLookCommand() {
        override val command = "savebmp $size $width"
    }

    data class SaveBitmapData(val data: String) : ActiveLookCommand() {
        override val command = data
    }

    data class Bitmap(val bitmapNumber: Int, val x: Int, val y: Int) : ActiveLookCommand() {
        // Currently the X/Y position needs adjusting due to physical screen orientation
        override val command = "bitmap $bitmapNumber ${Screen.MAX_WIDTH - x} ${Screen.MAX_HEIGHT - y}"
    }

    data class EraseBitmap(val bitmapNumber: Int) : ActiveLookCommand() {
        override val command = "erasebmp $bitmapNumber"
    }

    object EraseAllBitmaps : ActiveLookCommand() {
        override val command = "erasebmp all"
    }

    // endregion Image

    // region Notifications
    sealed class Notify(val service: Service, val characteristic: Characteristic): ActiveLookCommand() {

        object BatteryLevel: Notify(Service.Battery, Characteristic.BatteryLevel) {
            override val command = ""
        }

        object TxServer: Notify(Service.CommandInterface, Characteristic.TxServer) {
            override val command = ""
        }

        object Flow : Notify(Service.CommandInterface, Characteristic.FlowControl) {
            override val command = ""
        }
    }
    // endregion Notifications
    // region Layout

    data class SaveLayout(
        val layout: Layout
    ) : ActiveLookCommand() {

        override val command: String
            get() {
                return "savelayout 0x${layout.mapToCommand()}"
            }
    }

    data class SaveLayoutBytes(
            val layoutData: String
    ) : ActiveLookCommand() {

        override val command: String
            get() {
                return "savelayout 0x${layoutData}"
            }
    }

    data class EraseLayout(
        val layoutId: Int
    ) : ActiveLookCommand() {
        override val command: String = "eraselayout $layoutId"
    }

    data class DisplayLayout(val layoutId: Int, val text: String) : ActiveLookCommand() {
        override val command: String = "layout $layoutId $text"
    }

    // endregion Layout

    // region gauge

    data class GaugeValue(val gaugeNumber: Int, val value: Int) : ActiveLookCommand() {
        override val command: String = "gauge $gaugeNumber $value"
    }

    data class GaugeConfigure(val gaugeNumber: Int, val x: Int, val y: Int, val radius: Int, val radiusInner: Int,
                              val start: Int, val end: Int, val clockwise: Boolean) : ActiveLookCommand() {
        override val command: String
            get() {
                if(clockwise) {
                    return "savegauge $gaugeNumber $x $y $radius $radiusInner $start $end 0"
                } else {
                    return "savegauge $gaugeNumber $x $y $radius $radiusInner $start $end 1"
                }
            }
    }

    // endregion gauge
}

/**
 * This converts a command string to a [ByteArray] for writing
 * It appends '\u0000' as required by the ActiveLook specification
 */
internal fun ActiveLookCommand.data(): ByteArray {
    return (command + "\u0000").toByteArray(Charset.forName("UTF-8"))
}