package net.activelook.sdk.command

import android.graphics.Point
import android.graphics.Rect
import java.nio.charset.Charset

internal sealed class ActiveLookCommand: Enqueueable {

    abstract val command: String

    // region System
    object BatteryLevel: ActiveLookCommand() {
        override val command = "battery"
    }
    // endregion System

    // region Drawing

    // region Misc
    object Clear: ActiveLookCommand() {
        override val command = "clear"
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
}

internal fun ActiveLookCommand.data(): ByteArray {
    return (command + "\u0000").toByteArray(Charset.forName("UTF-8"))
}