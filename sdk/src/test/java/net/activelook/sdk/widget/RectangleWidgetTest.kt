package net.activelook.sdk.widget

import net.activelook.sdk.screen.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class RectangleWidgetTest {

    @Test
    fun `init RectangleWidget`() {
        var widget = RectangleWidget(15, 16, 20, 30, true, Color("#151413"))

        assertEquals(15, widget.x)
        assertEquals(16, widget.y)
        assertEquals(20, widget.height)
        assertEquals(30, widget.width)
        assertEquals(Color("#151413"), widget.color)

        widget = RectangleWidget(42, 24, 15, 35, false)

        assertEquals(42, widget.x)
        assertEquals(24, widget.y)
        assertEquals(15, widget.height)
        assertEquals(35, widget.width)
        assertEquals(null, widget.color)

        widget = RectangleWidget(4, 8, 28, 65)

        assertEquals(4, widget.x)
        assertEquals(8, widget.y)
        assertEquals(28, widget.height)
        assertEquals(65, widget.width)
        assertEquals(null, widget.color)
    }
}