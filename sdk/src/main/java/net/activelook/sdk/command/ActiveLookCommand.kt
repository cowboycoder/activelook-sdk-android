package net.activelook.sdk.command

import android.graphics.Point
import android.graphics.Rect
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex
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

internal sealed class ActiveLookCommand {

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

        override val command: String
            get() {
                if(filled) {
                    return "rectf ${rect.left} ${rect.top} ${rect.bottom} ${rect.right}"
                } else {
                    return "rect ${rect.left} ${rect.top} ${rect.bottom} ${rect.right}"
                }
            }
    }
    // endregion Shapes

    // region Text
    class Text(text: String, origin: Point, rotation: Int, fontSize: Int, color: Int): ActiveLookCommand() {
        override val command = "txt ${origin.x} ${origin.y} $rotation $fontSize $color $text"
    }
    // endregion Text

    // region Image

    object ListBitmaps : ActiveLookCommand() {
        override val command: String = "bmplist"
    }

    data class SaveBitmap(val size: Int, val width: Int, val bitmap: String) : ActiveLookCommand() {
        override val command = "savebmp $size $width 0x${bitmap.toHex()}"
    }

    data class Bitmap(val bitmapNumber: Int, val x: Int, val y: Int) : ActiveLookCommand() {
        override val command = "bitmap $bitmapNumber $x $y"
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
    }
    // endregion Notifications
    // region Layout

    data class SaveLayout(
        val screen: Screen
    ) : ActiveLookCommand(), NeedPreviousResults {

        override fun setPreviousResults(results: List<String>) {
            screen.widgets
        }

        override val command: String
            get() {
                return "savelayout 0x${screen.mapToCommand()}"
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
}

/**
 * This converts a command string to a [ByteArray] for writing
 * It appends '\u0000' as required by the ActiveLook specification
 */
internal fun ActiveLookCommand.data(): ByteArray {
    return (command + "\u0000").toByteArray(Charset.forName("UTF-8"))
}