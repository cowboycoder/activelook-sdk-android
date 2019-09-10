package net.activelook.sdk.screen

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetTest {

    @Test
    fun `get command size`() {
        val widget: Widget = TextWidget(15, 15, "Test")

        assertEquals(widget.command.length / 2, widget.getCommandSize())
    }

    @Test
    fun `set padding`() {
        val hasPosition: HasPosition = TextWidget(15, 15, "Test")

        assertEquals(0, hasPosition.paddingLeft)
        assertEquals(0, hasPosition.paddingTop)
        assertEquals(0, hasPosition.paddingRight)
        assertEquals(0, hasPosition.paddingBottom)

        hasPosition.setPadding(15, 16, 23, 42)

        assertEquals(15, hasPosition.paddingLeft)
        assertEquals(16, hasPosition.paddingTop)
        assertEquals(23, hasPosition.paddingRight)
        assertEquals(42, hasPosition.paddingBottom)

        hasPosition.setPadding(15)

        assertEquals(15, hasPosition.paddingLeft)
        assertEquals(16, hasPosition.paddingTop)
        assertEquals(23, hasPosition.paddingRight)
        assertEquals(42, hasPosition.paddingBottom)

        hasPosition.setPadding(right = 33)

        assertEquals(15, hasPosition.paddingLeft)
        assertEquals(16, hasPosition.paddingTop)
        assertEquals(33, hasPosition.paddingRight)
        assertEquals(42, hasPosition.paddingBottom)

        hasPosition.setPadding(top = 6)

        assertEquals(15, hasPosition.paddingLeft)
        assertEquals(6, hasPosition.paddingTop)
        assertEquals(33, hasPosition.paddingRight)
        assertEquals(42, hasPosition.paddingBottom)

        hasPosition.setPadding(bottom = 110)

        assertEquals(15, hasPosition.paddingLeft)
        assertEquals(6, hasPosition.paddingTop)
        assertEquals(33, hasPosition.paddingRight)
        assertEquals(110, hasPosition.paddingBottom)
    }

}