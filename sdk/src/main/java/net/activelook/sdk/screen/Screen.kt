package net.activelook.sdk.screen

import kotlin.math.max
import kotlin.math.min

class Screen private constructor(
    val id: Int,
    val paddingLeft: Int,
    val paddingTop: Int,
    val paddingRight: Int,
    val paddingBottom: Int,
    val widgets: List<Widget>
) {
    class Builder private constructor() {

        private var id: Int = -1

        private var paddingLeft = 0
        private var paddingRight = 0
        private var paddingTop = 0
        private var paddingBottom = 0

        private val widgets: MutableList<Widget> = mutableListOf()

        companion object {
            private const val ID_MIN = 10
            private const val ID_MAX = 59
        }

        constructor(id: Int) : this() {
            this.id = max(min(id, ID_MAX), ID_MIN)
        }

        fun setPadding(left: Int, top: Int, right: Int, bottom: Int): Builder {
            paddingLeft = left
            paddingTop = top
            paddingRight = right
            paddingBottom = bottom

            return this
        }

        fun addWidget(widget: Widget): Builder {
            widgets.add(widget)
            return this
        }

        fun build(): Screen {
            return Screen(
                id,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                widgets
            )
        }
    }
}

