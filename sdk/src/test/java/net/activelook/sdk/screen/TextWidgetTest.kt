package net.activelook.sdk.screen

import net.activelook.sdk.Font
import org.junit.Assert.assertEquals
import org.junit.Test

class TextWidgetTest {

    @Test
    fun `init TextWidget`() {
        var textWidget = TextWidget(15, 16, "Test", Font.SMALL, Color("#151413"))

        assertEquals(15, textWidget.x)
        assertEquals(16, textWidget.y)
        assertEquals("Test", textWidget.text)
        assertEquals(Font.SMALL, textWidget.font)
        assertEquals(Color("#151413"), textWidget.color)

        textWidget = TextWidget(42, 24, "Test2", Font.LARGE)

        assertEquals(42, textWidget.x)
        assertEquals(24, textWidget.y)
        assertEquals("Test2", textWidget.text)
        assertEquals(Font.LARGE, textWidget.font)
        assertEquals(null, textWidget.color)

        textWidget = TextWidget(4, 8, "Test3")

        assertEquals(4, textWidget.x)
        assertEquals(8, textWidget.y)
        assertEquals("Test3", textWidget.text)
        assertEquals(null, textWidget.font)
        assertEquals(null, textWidget.color)
    }

    @Test
    fun `generate command`() {
        var textWidget = TextWidget(15, 16, "Test", Font.SMALL, Color("#151413"))

        assertEquals("0301040109000F00100454657374", textWidget.command)

        textWidget = TextWidget(42, 24, "Test2", Font.LARGE)

        assertEquals("040309002A0018055465737432", textWidget.command)

        textWidget = TextWidget(4, 8, "Test3")

        assertEquals("0900040008055465737433", textWidget.command)
    }

}