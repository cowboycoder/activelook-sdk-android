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
        assertEquals(0, textWidget.paddingLeft)
        assertEquals(0, textWidget.paddingTop)
        assertEquals(0, textWidget.paddingRight)
        assertEquals(0, textWidget.paddingBottom)

        textWidget = TextWidget(42, 24, "Test2", Font.LARGE)

        assertEquals(42, textWidget.x)
        assertEquals(24, textWidget.y)
        assertEquals("Test2", textWidget.text)
        assertEquals(Font.LARGE, textWidget.font)
        assertEquals(null, textWidget.color)
        assertEquals(0, textWidget.paddingLeft)
        assertEquals(0, textWidget.paddingTop)
        assertEquals(0, textWidget.paddingRight)
        assertEquals(0, textWidget.paddingBottom)

        textWidget = TextWidget(4, 8, "Test3")
        textWidget.setPadding(4, 8, 15, 16)

        assertEquals(4, textWidget.x)
        assertEquals(8, textWidget.y)
        assertEquals("Test3", textWidget.text)
        assertEquals(null, textWidget.font)
        assertEquals(null, textWidget.color)
        assertEquals(4, textWidget.paddingLeft)
        assertEquals(8, textWidget.paddingTop)
        assertEquals(15, textWidget.paddingRight)
        assertEquals(16, textWidget.paddingBottom)
    }

    @Test
    fun `generate command`() {
        var textWidget =
            TextWidget(15, 16, "Test", Font.SMALL, Color("#151413"))

        assertEquals("0301040109012000EF0454657374", textWidget.command)

        textWidget = TextWidget(42, 24, "Test2", Font.LARGE)

        assertEquals("040309010500E7055465737432", textWidget.command)

        textWidget = TextWidget(4, 8, "Test3")

        assertEquals("09012B00F7055465737433", textWidget.command)
    }

}