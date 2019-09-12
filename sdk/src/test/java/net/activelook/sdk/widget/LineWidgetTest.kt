package net.activelook.sdk.widget

import net.activelook.sdk.screen.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class LineWidgetTest {

    @Test
    fun `init LineWidget`() {
        var widget = LineWidget(15, 16, 10, 142, Color("#151413"))

        assertEquals(15, widget.x)
        assertEquals(16, widget.y)
        assertEquals(15, widget.x0)
        assertEquals(16, widget.y0)
        assertEquals(10, widget.x1)
        assertEquals(142, widget.y1)
        assertEquals(Color("#151413"), widget.color)
        assertEquals(0, widget.paddingLeft)
        assertEquals(0, widget.paddingTop)
        assertEquals(0, widget.paddingRight)
        assertEquals(0, widget.paddingBottom)

        widget = LineWidget(42, 24, 30, 92)

        assertEquals(42, widget.x)
        assertEquals(24, widget.y)
        assertEquals(42, widget.x0)
        assertEquals(24, widget.y0)
        assertEquals(30, widget.x1)
        assertEquals(92, widget.y1)
        assertEquals(null, widget.color)
        assertEquals(0, widget.paddingLeft)
        assertEquals(0, widget.paddingTop)
        assertEquals(0, widget.paddingRight)
        assertEquals(0, widget.paddingBottom)

        widget = LineWidget(4, 8, 80, 70)
        widget.setPadding(4, 8, 15, 16)

        assertEquals(4, widget.x)
        assertEquals(8, widget.y)
        assertEquals(4, widget.x0)
        assertEquals(8, widget.y0)
        assertEquals(80, widget.x1)
        assertEquals(70, widget.y1)
        assertEquals(null, widget.color)
        assertEquals(4, widget.paddingLeft)
        assertEquals(8, widget.paddingTop)
        assertEquals(15, widget.paddingRight)
        assertEquals(16, widget.paddingBottom)
    }

    @Test
    fun `generate command`() {
        var widget: Widget = LineWidget(15, 16, 10, 142, Color("#888888"))

        assertEquals("030805012000EF01250071", widget.command)

        widget = LineWidget(4, 8, 80, 70)

        assertEquals("05012B00F700DF00B9", widget.command)
    }

}