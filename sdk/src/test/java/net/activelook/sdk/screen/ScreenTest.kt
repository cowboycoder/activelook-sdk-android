package net.activelook.sdk.screen

import net.activelook.sdk.Font
import net.activelook.sdk.util.Point
import net.activelook.sdk.widget.TextWidget
import net.activelook.sdk.widget.Widget
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `init screen with just an id`() {
        val screen = Screen.Builder(1).build()

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
        assertEquals(screen.widgets, emptyList<Widget>())
    }

    @Test
    fun `init screen with an id and padding`() {
        val screen = Screen.Builder(1)
            .setPadding(4, 8, 15, 16)
            .build()

        assertEquals(10, screen.id)
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
        val screen = Screen.Builder(1)
            .addWidget(widget)
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
        val screen = Screen.Builder(1)
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
        val screen = Screen.Builder(1)
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
    fun `init screen with a background color`() {
        val screen = Screen.Builder(1)
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
        val screen = Screen.Builder(-5)
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
    fun `vertical offset test, multiview`() {
        val yOffs = Screen.HEIGHT / 2
        val multiViewTop = Screen.Builder(15, Screen.TOP_HALF)
                .setPadding(0, 0, 0, yOffs)
//                .setRelativePositions(true)
                .setBackgroundColor(0)
                .setForegroundColor(15)
                .setFont(Font.LARGE)
                .setText(Point(30, 30), Orientation.R4, true)
                .addWidget(TextWidget(250, 50, "Units", Font.SMALL))
                .addWidget(TextWidget(90, 90, "Title", Font.MEDIUM))

        val multiViewBottom = Screen.Builder(16, Screen.BOTTOM_HALF)
                .setPadding(0, yOffs, 0, 0)
//                .setRelativePositions(true)
                .setBackgroundColor(0)
                .setForegroundColor(15)
                .setFont(Font.LARGE)
                .setText(Point(30, 30), Orientation.R4, true)
                .addWidget(TextWidget(250, 50 + yOffs, "Units", Font.SMALL))
                .addWidget(TextWidget(90, 90 + yOffs, "Title", Font.MEDIUM))

        val cmd1 = multiViewTop.build().mapToLayout(15 + 9, 0).mapToCommand()
        val cmd2 = multiViewBottom.build().mapToLayout(16 + 9, 0).mapToCommand()

        assertEquals("181A000080012FFF0F0003010111E10401040109003500CD05556E69747304020900D500A5055469746C65", cmd1)
        assertEquals("191A000000012F7F0F00030101116104010401090035004D05556E69747304020900D50025055469746C65", cmd2)

//        assertEquals("1F1A000000012F7F0F00030101116104010401090035004D05556E69747304020900D50025055469746C65", cmd1)
//        assertEquals("201A000080012FFF0F0003010111E10401040109003500CD05556E69747304020900D500A5055469746C65", cmd2)
    }
}