package net.activelook.sdk.widget

import net.activelook.sdk.Font
import net.activelook.sdk.screen.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class TextWidgetTest {

    @Test
    fun `init TextWidget`() {
        var textWidget =
            TextWidget(15, 16, "Test", Font.SMALL, Color("#151413"))

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
}