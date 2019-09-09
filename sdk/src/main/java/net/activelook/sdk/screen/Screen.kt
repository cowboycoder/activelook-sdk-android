package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.util.Point
import net.activelook.sdk.util.toHex
import kotlin.math.max
import kotlin.math.min

class Screen private constructor(
    val id: Int,
    val x0: Int,
    val y0: Int,
    val x1: Int,
    val y1: Int,
    val foreground: Int,
    val background: Int,
    val font: Font,
    val textPosition: Point,
    val textOrientation: Orientation,
    val textOpacity: Boolean,
    val widgets: List<Widget>
) {

    companion object {
        const val WIDTH = 304
        const val MAX_WIDTH = WIDTH - 1

        const val HEIGHT = 256
        const val MAX_HEIGHT = HEIGHT - 1

        internal const val ID_MIN = 10
        internal const val ID_MAX = 59
        internal const val BACKGROUND_MIN = 0
        internal const val BACKGROUND_MAX = 15
        internal const val FOREGROUND_MIN = 0
        internal const val FOREGROUND_MAX = 15
    }

    class Builder private constructor() {

        private var id: Int = -1

        private var paddingLeft = 0
        private var paddingRight = 0
        private var paddingTop = 0
        private var paddingBottom = 0

        private var foreground: Int = 0
        private var background: Int = 15
        private var font: Font = Font.MEDIUM
        private var textPosition: Point = Point(0, 0)
        private var textRotation: Orientation = Orientation.R4
        private var textOpacity: Boolean = true

        private val widgets: MutableList<Widget> = mutableListOf()

        constructor(id: Int) : this() {
            this.id = max(min(id, ID_MAX), ID_MIN)
        }

        fun setPadding(left: Int, top: Int, right: Int, bottom: Int): Builder {
            this.paddingLeft = left
            this.paddingTop = top
            this.paddingRight = right
            this.paddingBottom = bottom

            return this
        }

        fun addWidget(widget: Widget): Builder {
            this.widgets.add(widget)
            return this
        }

        fun setBackgroundColor(color: Int): Builder {
            this.background = max(min(color, BACKGROUND_MAX), BACKGROUND_MIN)
            return this
        }

        fun setForegroundColor(color: Int): Builder {
            this.foreground = max(min(color, FOREGROUND_MAX), FOREGROUND_MIN)
            return this
        }

        fun setFont(font: Font): Builder {
            this.font = font
            return this
        }

        fun setText(position: Point, orientation: Orientation, isVisible: Boolean): Builder {
            this.textPosition = position
            this.textRotation = orientation
            this.textOpacity = isVisible
            return this
        }

        fun setTextPosition(position: Point): Builder {
            this.textPosition = position
            return this
        }

        fun setTextOrientation(orientation: Orientation): Builder {
            this.textRotation = orientation
            return this
        }

        fun setTextVisibility(isVisible: Boolean): Builder {
            this.textOpacity = isVisible
            return this
        }

        fun build(): Screen {
            val x0 = paddingLeft
            val y0 = paddingTop

            val x1 = MAX_WIDTH - paddingRight
            val y1 = MAX_HEIGHT - paddingBottom

            return Screen(
                id,
                x0,
                y0,
                x1,
                y1,
                foreground,
                background,
                font,
                textPosition,
                textRotation,
                textOpacity,
                widgets
            )
        }
    }

    internal fun mapToCommand(): String {
        val x0 = x0
        val y0 = y0
        val x1 = x1
        val y1 = y1

        val textX0 = textPosition.x
        val textY0 = textPosition.y

        val foregroundColor = foreground
        val backgroundColor = background
        val font = this.font.value
        val textValid = true
        val textRotation = textOrientation.value

        val maxAdditionalCommandsSize = 127 - 17

        var sizeAdditionalCommands = 0
        val additionalCommandsToAdd = mutableListOf<Widget>()

        for (additionalCommand in widgets) {
            if (sizeAdditionalCommands + additionalCommand.getCommandSize() > maxAdditionalCommandsSize) {
                break
            }
            additionalCommandsToAdd.add(additionalCommand)
            sizeAdditionalCommands += additionalCommand.getCommandSize()
        }

        return "${id.toHex()}${sizeAdditionalCommands.toHex()}" +
                "${x0.toHex(4)}${y0.toHex()}${x1.toHex(4)}${y1.toHex()}" +
                "${foregroundColor.toHex()}${backgroundColor.toHex()}${font.toHex()}" +
                "${textValid.toHex()}${textX0.toHex(4)}${textY0.toHex()}" +
                "${textRotation.toHex()}${textOpacity.toHex()}" +
                additionalCommandsToAdd.joinToString(separator = "") { it.command }
    }
}

