package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.layout.Layout
import net.activelook.sdk.util.Point
import net.activelook.sdk.widget.BitmapWidget
import net.activelook.sdk.widget.Widget
import kotlin.math.max
import kotlin.math.min

/**
 * A representation of a view that can display different [Widget]s
 * like text, circle, rectangle, etc.
 */
class Screen private constructor(
    /**
     * Id of the screen, starts with 10
     */
    val id: Int,
    /**
     * Left padding
     */
    val x0: Int,
    /**
     * Top padding
     */
    val y0: Int,
    /**
     * Right padding
     */
    val x1: Int,
    /**
     * Bottom padding
     */
    val y1: Int,
    /**
     * Foreground color
     */
    val foreground: Int,
    /**
     * Background color
     */
    val background: Int,
    /**
     * Font used
     */
    val font: Font,
    /**
     * Position of the variable text
     */
    val textPosition: Point,
    /**
     * Orientation of every text
     */
    val textOrientation: Orientation,
    /**
     * Opacity of the text
     */
    val textOpacity: Boolean,
    /**
     * Additional widgets
     */
    val widgets: List<Widget>
) {

    companion object {
        /**
         * Width of the screen
         */
        const val WIDTH = 304
        /**
         * Maximum width possible
         */
        const val MAX_WIDTH = WIDTH - 1

        /**
         * Height of the screen
         */
        const val HEIGHT = 256
        /**
         * Maximum height possible
         */
        const val MAX_HEIGHT = HEIGHT - 1

        internal const val ID_MIN = 10
        internal const val ID_MAX = 59
        internal const val BACKGROUND_MIN = 0
        internal const val BACKGROUND_MAX = 15
        internal const val FOREGROUND_MIN = 0
        internal const val FOREGROUND_MAX = 15

        internal const val SIZE_ADDITIONAL_COMMANDS_MAX = 127 - 17

        @JvmStatic
        fun isOnScreen(point: android.graphics.Point): Boolean {
            if(point.x < 0)
                return false
            if(point.x > MAX_WIDTH)
                return false
            if(point.y < 0)
                return false
            if(point.y > MAX_HEIGHT)
                return false
            return true
        }
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

        /**
         * Create a Builder with property initialized with values from the JSON.
         *
         * If the version of the JSON is invalid, it will throw a `JsonVersionInvalidException`.
         *
         * If there are mission or misspelled properties, it will throw a `JsonInvalidException`.
         */
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

        /**
         * Build a [screen][Screen] with the arguments supplied to this builder.
         */
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

    internal fun mapToLayout(startLayoutId: Int, startBitmapId: Int): Layout {
        val widgets =
            widgets.filter { it !is BitmapWidget }.flatMap { it.mapToLayoutWidget(x0, y0) }
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

