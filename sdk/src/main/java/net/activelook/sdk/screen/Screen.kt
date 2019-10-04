package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.layout.Layout
import net.activelook.sdk.util.Point
import net.activelook.sdk.widget.BitmapWidget
import net.activelook.sdk.widget.HasPosition
import net.activelook.sdk.widget.Widget
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

        internal const val SIZE_ADDITIONAL_COMMANDS_MAX = 127 - 17
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

        internal constructor(id: Int) : this() {
            val shifted = id + ID_MIN - 1
            setId(shifted)
        }

        constructor(rawJsonContent: String) : this() {
            val builder = ScreenParser.parse(rawJsonContent)
            copy(builder)
        }

        internal fun copy(builder: Builder): Builder {
            setId(builder.id)
            setPadding(
                builder.paddingLeft,
                builder.paddingTop,
                builder.paddingRight,
                builder.paddingBottom
            )
            setBackgroundColor(builder.background)
            setForegroundColor(builder.foreground)
            setFont(builder.font)
            setText(builder.textPosition, builder.textRotation, builder.textOpacity)

            for (widget in builder.widgets) {
                if (!this.widgets.contains(widget)) {
                    addWidget(widget)
                }
            }

            return this
        }

        internal fun setId(id: Int) {
            this.id = max(min(id, ID_MAX), ID_MIN)
        }

        internal fun setPadding(left: Int, top: Int, right: Int, bottom: Int): Builder {
            this.paddingLeft = left
            this.paddingTop = top
            this.paddingRight = right
            this.paddingBottom = bottom

            return this
        }

        internal fun addWidget(widget: Widget): Builder {
            this.widgets.add(widget)
            return this
        }

        internal fun setBackgroundColor(color: Int): Builder {
            this.background = max(min(color, BACKGROUND_MAX), BACKGROUND_MIN)
            return this
        }

        internal fun setForegroundColor(color: Int): Builder {
            this.foreground = max(min(color, FOREGROUND_MAX), FOREGROUND_MIN)
            return this
        }

        internal fun setFont(font: Font): Builder {
            this.font = font
            return this
        }

        internal fun setText(
            position: Point,
            orientation: Orientation,
            isVisible: Boolean
        ): Builder {
            this.textPosition = position
            this.textRotation = orientation
            this.textOpacity = isVisible
            return this
        }

        internal fun setTextPosition(position: Point): Builder {
            this.textPosition = position
            return this
        }

        internal fun setTextOrientation(orientation: Orientation): Builder {
            this.textRotation = orientation
            return this
        }

        internal fun setTextVisibility(isVisible: Boolean): Builder {
            this.textOpacity = isVisible
            return this
        }

        fun build(): Screen {
            val x0 = paddingLeft
            val y0 = paddingTop

            val x1 = MAX_WIDTH - paddingRight
            val y1 = MAX_HEIGHT - paddingBottom

            for (widget in widgets) {
                if (widget is HasPosition) {
                    widget.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
                }
            }

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

    internal fun mapToLayout(startLayoutId: Int, startBitmapId: Int): Layout {
        val widgets = widgets.filter { it !is BitmapWidget }.flatMap { it.mapToLayoutWidget() }
        val layout = Layout(
            startLayoutId,
            x0,
            y0,
            x1,
            y1,
            foreground,
            background,
            font.value,
            true,
            textPosition.x,
            textPosition.y,
            textOrientation.value,
            textOpacity,
            widgets
        )

        return layout
    }
}

