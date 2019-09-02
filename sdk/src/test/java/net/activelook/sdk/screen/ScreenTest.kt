package net.activelook.sdk.screen

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `init screen with just an id`() {
        val screen = Screen.Builder(15).build()

        assertEquals(15, screen.id)
        assertEquals(0, screen.paddingLeft)
        assertEquals(0, screen.paddingTop)
        assertEquals(0, screen.paddingRight)
        assertEquals(0, screen.paddingBottom)
        assertEquals(screen.widgets, emptyList<Widget>())
    }

    @Test
    fun `init screen with an id and padding`() {
        val screen = Screen.Builder(15)
            .setPadding(4, 8, 15, 16)
            .build()

        assertEquals(15, screen.id)
        assertEquals(4, screen.paddingLeft)
        assertEquals(8, screen.paddingTop)
        assertEquals(15, screen.paddingRight)
        assertEquals(16, screen.paddingBottom)
        assertEquals(emptyList<Widget>(), screen.widgets)
    }

    @Test
    fun `init screen with an id and a text widget`() {
        val widget = TextWidget(0, 0, Orientation.R2, "#142414", "Font", 14)
        val screen = Screen.Builder(15)
            .addWidget(widget)
            .build()

        assertEquals(15, screen.id)
        assertEquals(0, screen.paddingLeft)
        assertEquals(0, screen.paddingTop)
        assertEquals(0, screen.paddingRight)
        assertEquals(0, screen.paddingBottom)
        assertEquals(listOf(widget), screen.widgets)
    }

    @Test
    fun `init screen with a wrong id, padding and a text widget`() {
        val widget = TextWidget(0, 0, Orientation.R7, "#142414", "Font", 14)
        val screen = Screen.Builder(1)
            .setPadding(23, 42, 4, 8)
            .addWidget(widget)
            .build()

        assertEquals(10, screen.id)
        assertEquals(23, screen.paddingLeft)
        assertEquals(42, screen.paddingTop)
        assertEquals(4, screen.paddingRight)
        assertEquals(8, screen.paddingBottom)
        assertEquals(listOf(widget), screen.widgets)
    }
}