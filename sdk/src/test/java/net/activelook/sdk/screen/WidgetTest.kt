package net.activelook.sdk.screen

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetTest {

    @Test
    fun `get command size`() {
        val widget = TextWidget(15, 15, "Test")

        assertEquals(widget.command.length / 2, widget.getCommandSize())
    }

}