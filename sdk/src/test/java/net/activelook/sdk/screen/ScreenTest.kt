package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.util.Point
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `init screen with just an id`() {
        val screen = Screen.Builder(15).build()

        assertEquals(15, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
        assertEquals(screen.widgets, emptyList<Widget>())
    }

    @Test
    fun `init screen with an id and padding`() {
        val screen = Screen.Builder(15)
            .setPadding(4, 8, 15, 16)
            .build()

        assertEquals(15, screen.id)
        assertEquals(4, screen.x0)
        assertEquals(8, screen.y0)
        assertEquals(288, screen.x1)
        assertEquals(239, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
        assertEquals(emptyList<Widget>(), screen.widgets)
    }

    @Test
    fun `init screen with an id and a text widget`() {
        val widget = TextWidget(0, 0, "Sample")
        val screen = Screen.Builder(15)
            .addWidget(widget)
            .build()

        assertEquals(15, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
        assertEquals(listOf(widget), screen.widgets)
    }

    @Test
    fun `init screen with a variable text configuration`() {
        val screen = Screen.Builder(1)
            .setPadding(23, 42, 4, 8)
            .setText(Point(15, 25), Orientation.R7, false)
            .build()

        assertEquals(10, screen.id)
        assertEquals(23, screen.x0)
        assertEquals(42, screen.y0)
        assertEquals(299, screen.x1)
        assertEquals(247, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(15, 25), screen.textPosition)
        assertEquals(Orientation.R7, screen.textOrientation)
        assertEquals(false, screen.textOpacity)
    }

    @Test
    fun `init screen with text position`() {
        val screen = Screen.Builder(1)
            .setPadding(23, 42, 4, 8)
            .setTextPosition(Point(16, 42))
            .build()

        assertEquals(10, screen.id)
        assertEquals(23, screen.x0)
        assertEquals(42, screen.y0)
        assertEquals(299, screen.x1)
        assertEquals(247, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(16, 42), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with text orientation`() {
        val screen = Screen.Builder(1)
            .setPadding(23, 42, 4, 8)
            .setTextOrientation(Orientation.R2)
            .build()

        assertEquals(10, screen.id)
        assertEquals(23, screen.x0)
        assertEquals(42, screen.y0)
        assertEquals(299, screen.x1)
        assertEquals(247, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R2, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with text visibility`() {
        val screen = Screen.Builder(1)
            .setPadding(23, 42, 4, 8)
            .setTextVisibility(false)
            .build()

        assertEquals(10, screen.id)
        assertEquals(23, screen.x0)
        assertEquals(42, screen.y0)
        assertEquals(299, screen.x1)
        assertEquals(247, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(false, screen.textOpacity)
    }

    @Test
    fun `init screen with no foreground color`() {
        val screen = Screen.Builder(1)
            .build()

        assertEquals(10, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with a foreground color`() {
        val screen = Screen.Builder(10)
            .setForegroundColor(7)
            .build()

        assertEquals(10, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(7, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with a wrong foreground color`() {
        val screen = Screen.Builder(10)
            .setForegroundColor(-15)
            .build()

        assertEquals(10, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with no background color`() {
        val screen = Screen.Builder(10)
            .build()

        assertEquals(10, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(15, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with a background color`() {
        val screen = Screen.Builder(10)
            .setBackgroundColor(7)
            .build()

        assertEquals(10, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(7, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with a wrong background color`() {
        val screen = Screen.Builder(1)
            .setBackgroundColor(-15)
            .build()

        assertEquals(10, screen.id)
        assertEquals(0, screen.x0)
        assertEquals(0, screen.y0)
        assertEquals(303, screen.x1)
        assertEquals(255, screen.y1)
        assertEquals(0, screen.background)
        assertEquals(0, screen.foreground)
        assertEquals(Font.MEDIUM, screen.font)
        assertEquals(Point(0, 0), screen.textPosition)
        assertEquals(Orientation.R4, screen.textOrientation)
        assertEquals(true, screen.textOpacity)
    }

    @Test
    fun `init screen with a wrong id, padding and a text widget`() {
        val widget = TextWidget(15, 42, "Sample")
        val screen = Screen.Builder(1)
            .setPadding(23, 42, 4, 8)
            .addWidget(widget)
            .build()

        assertEquals(10, screen.id)
        assertEquals(23, screen.x0)
        assertEquals(42, screen.y0)
        assertEquals(299, screen.x1)
        assertEquals(247, screen.y1)
        assertEquals(listOf(widget), screen.widgets)
    }

    @Test
    fun `map a screen to command`() {
        var screen = Screen.Builder(10)
            .setPadding(0, 0, 0, 0)
            .setForegroundColor(15)
            .setBackgroundColor(0)
            .setFont(Font.LARGE)
            .setText(Point(224, 128), Orientation.R4, true)
            .build()

        assertEquals("0A00000000012FFF0F00030100E0800401", screen.mapToCommand())

        screen = Screen.Builder(10)
            .setPadding(0, 0, 0, 0)
            .setForegroundColor(15)
            .setBackgroundColor(0)
            .setFont(Font.LARGE)
            .setText(Point(224, 128), Orientation.R4, true)
            .addWidget(TextWidget(15, 16, "Test"))
            .build()

        assertEquals(
            "0A0A000000012FFF0F00030100E080040109000F00100454657374",
            screen.mapToCommand()
        )

        screen = Screen.Builder(10)
            .setPadding(0, 0, 0, 0)
            .setForegroundColor(15)
            .setBackgroundColor(0)
            .setFont(Font.LARGE)
            .setText(Point(224, 128), Orientation.R4, true)
            .addWidget(TextWidget(15, 16, "Test2"))
            .addWidget(TextWidget(15, 16, "Test3"))
            .addWidget(TextWidget(15, 16, "Test4"))
            .addWidget(TextWidget(15, 16, "Test5"))
            .addWidget(TextWidget(15, 16, "Test6"))
            .addWidget(TextWidget(15, 16, "Test7"))
            .addWidget(TextWidget(15, 16, "Test8"))
            .addWidget(TextWidget(15, 16, "Test9"))
            .addWidget(TextWidget(15, 16, "Test10"))
            .addWidget(TextWidget(15, 16, "Test11"))
            .addWidget(TextWidget(15, 16, "Test12"))
            .addWidget(TextWidget(15, 16, "Test13"))
            .build()

        assertEquals(
            "0A64000000012FFF0F00030100E080040109000F001005546573743209000F001005546573743309000F001005546573743409000F001005546573743509000F001005546573743609000F001005546573743709000F001005546573743809000F001005546573743909000F001006546573743130",
            screen.mapToCommand()
        )
    }


}