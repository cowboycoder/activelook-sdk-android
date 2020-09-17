package net.activelook.sdk.screen

import android.graphics.Rect
import net.activelook.sdk.Font
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.layout.Layout
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.util.Point
import net.activelook.sdk.widget.BitmapWidget
import net.activelook.sdk.widget.TextWidget
import net.activelook.sdk.widget.Widget
import kotlin.math.max
import kotlin.math.min

/**
 * A representation of a view that can display different [Widget]s
 * like text, circle, rectangle, etc.
 */
open class Screen private constructor(
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

        @JvmStatic
        val TOP_HALF = Rect(0, 0, MAX_WIDTH, (HEIGHT / 2) - 1)
        @JvmStatic
        val BOTTOM_HALF = Rect(0, (HEIGHT / 2), MAX_WIDTH, MAX_HEIGHT)
        @JvmStatic
        val TOP_HALF_PAD = Rect(0, 0, 0, HEIGHT / 2)
        @JvmStatic
        val BOTTOM_HALF_PAD = Rect(0, MAX_HEIGHT / 2, 0, 0)

        internal const val ID_MIN = 10
        internal const val ID_MAX = 59
        internal const val BACKGROUND_MIN = 0
        internal const val BACKGROUND_MAX = 15
        internal const val FOREGROUND_MIN = 0
        internal const val FOREGROUND_MAX = 15

        internal const val SIZE_ADDITIONAL_COMMANDS_MAX = 127 - 17

        @JvmStatic
        fun isOnScreen(point: Point): Boolean {
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
        private var clipRect : Rect? = null

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

        constructor(id: Int, region: Rect) : this(id) {
            // Convert rect to internal 'padding'
            setPadding(region.left, region.top,
                    Screen.MAX_WIDTH - region.right, Screen.MAX_HEIGHT - region.bottom)
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

        fun getId() : Int {
            return this.id
        }

        fun setClipRect(region: Rect) : Builder {
            this.clipRect = region
            return this
        }

        internal fun setPadding(left: Int, top: Int, right: Int, bottom: Int): Builder {
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

        fun clearWidgets(): Builder {
            this.widgets.clear()
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

        fun setText(
            position: Point,
            orientation: Orientation,
            isVisible: Boolean
        ): Builder {
            if(clipRect != null) {
//                setPadding(region.left, region.top,
//                    Screen.MAX_WIDTH - region.right, Screen.MAX_HEIGHT - region.bottom)
                this.textPosition = position.offset(clipRect!!.left, clipRect!!.top)
            } else {
                this.textPosition = position
            }
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

        fun toOperationList(withText: String): Array<ActiveLookCommand> {
            val commands : MutableList<ActiveLookCommand> = mutableListOf(
                    ActiveLookCommand.Color(background),
                    ActiveLookCommand.Rectangle(paddingLeft, paddingTop, MAX_WIDTH - paddingRight, MAX_HEIGHT - paddingBottom, true),
                    ActiveLookCommand.Color(foreground),
                    ActiveLookCommand.Rectangle(paddingLeft, paddingTop, MAX_WIDTH - paddingRight, MAX_HEIGHT - paddingBottom, false),
                    ActiveLookCommand.Text(withText, textPosition.offset(paddingLeft, paddingTop), textRotation.value, font.value, foreground)
                )
            widgets.forEach {
                when(it) {
                    is TextWidget -> { commands += ActiveLookCommand.Text(it.text, it.position.offset(paddingLeft, paddingTop), textRotation.value, it.font!!.value, foreground)}
                }
            }
            return commands.toTypedArray()
        }
    }

    fun mapToLayout(startLayoutId: Int, startBitmapId: Int): Layout {
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

