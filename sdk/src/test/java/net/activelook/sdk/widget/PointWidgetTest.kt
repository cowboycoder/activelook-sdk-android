package net.activelook.sdk.widget

import net.activelook.sdk.screen.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class PointWidgetTest {

    @Test
    fun `init PointWidget`() {
        var widget = PointWidget(15, 16, Color("#151413"))

        assertEquals(15, widget.x)
        assertEquals(16, widget.y)
        assertEquals(Color("#151413"), widget.color)
        assertEquals(0, widget.paddingLeft)
        assertEquals(0, widget.paddingTop)
        assertEquals(0, widget.paddingRight)
        assertEquals(0, widget.paddingBottom)

        widget = PointWidget(42, 24)

        assertEquals(42, widget.x)
        assertEquals(24, widget.y)
        assertEquals(null, widget.color)
        assertEquals(0, widget.paddingLeft)
        assertEquals(0, widget.paddingTop)
        assertEquals(0, widget.paddingRight)
        assertEquals(0, widget.paddingBottom)

        widget = PointWidget(4, 8)
        widget.setPadding(4, 8, 15, 16)

        assertEquals(4, widget.x)
        assertEquals(8, widget.y)
        assertEquals(null, widget.color)
        assertEquals(4, widget.paddingLeft)
        assertEquals(8, widget.paddingTop)
        assertEquals(15, widget.paddingRight)
        assertEquals(16, widget.paddingBottom)
    }
}