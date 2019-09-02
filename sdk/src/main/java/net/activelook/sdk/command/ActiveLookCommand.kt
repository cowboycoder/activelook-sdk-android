package net.activelook.sdk.command

import android.graphics.Point
import android.graphics.Rect
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

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
    // TODO:
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

    class SaveLayout(
        private val id: Int,
        private val x0: Int, // 2 bytes
        private val y0: Int,
        private val x1: Int, // 2 bytes
        val y1: Int,
        val foregroundColor: Int,
        val backgroundColor: Int,
        val font: String,
        val textValid: Boolean,
        val textX0: Int, // 2 bytes
        val textY0: Int,
        val textRotation: Int,
        val textOpacity: Boolean,
        private vararg val additionalCommands: AdditionalCommand
    ) : ActiveLookCommand() {

        private val maxAdditionalCommandsSize = 127 - 17

        override val command: String
            get() {
                var sizeAdditionalCommands = 0
                val additionalCommandsToAdd = mutableListOf<AdditionalCommand>()

                for (additionalCommand in additionalCommands) {
                    if (sizeAdditionalCommands + additionalCommand.size > maxAdditionalCommandsSize) {
                        break
                    }
                    additionalCommandsToAdd.add(additionalCommand)
                    sizeAdditionalCommands += additionalCommand.size
                }

                return "savelayout 0x${id.toHex()}${sizeAdditionalCommands.toHex()}" +
                        "${x0.toHex(4)}${y0.toHex()}${x1.toHex(4)}${y1.toHex()}" +
                        "${foregroundColor.toHex()}${backgroundColor.toHex()}${font.toInt().toHex()}" +
                        "${textValid.toHex()}${textX0.toHex(4)}${textY0.toHex()}" +
                        "${textRotation.toHex()}${textOpacity.toHex()}" +
                        additionalCommands.joinToString(separator = "") { it.command }
            }

    }

    interface AdditionalCommand {
        val id: Int
        val size: Int
        val command: String
    }

    class BitmapLayout(
        val bitmapId: Int,
        val x0: Int, // 2 bytes
        val y0: Int // 2 bytes
    ) : AdditionalCommand {
        override val id: Int = 0

        override val command: String
            get() {
                return "${id.toHex()}${bitmapId.toHex()}${x0.toHex(4)}${y0.toHex(4)}"
            }

        override val size: Int = 6
    }

    class FontLayout(
        val font: String
    ) : AdditionalCommand {
        override val id: Int = 4

        override val command: String
            get() {
                return "${id.toHex()}${font.toInt().toHex()}"
            }

        override val size: Int = 2
    }

    class TextLayout(
        val x0: Int, // 2 bytes
        val y0: Int, // 2 bytes
        val text: String
    ) : AdditionalCommand {

        init {
            val size = command.length
            print(size)
        }

        override val id: Int = 9

        override val command: String
            get() {
                return "${id.toHex()}${x0.toHex(4)}${y0.toHex(4)}" +
                        "${text.length.toHex()}${text.toHex()}"
            }

        override val size: Int = 6 + text.toHex().length / 2
    }

    // endregion Layout
}

internal fun String.toHex() = this.toByteArray().joinToString("") { "%02X".format(it) }


internal fun Int.toHex(length: Int = 2): String {
    return "%0${length}X".format(this)
}

internal fun Boolean.toHex(): String {
    return if (this) {
        "01"
    } else {
        "00"
    }
}

/**
 * This converts a command string to a [ByteArray] for writing
 * It appends '\u0000' as required by the ActiveLook specification
 */
internal fun ActiveLookCommand.data(): ByteArray {
    return (command + "\u0000").toByteArray(Charset.forName("UTF-8"))
}