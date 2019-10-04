package net.activelook.sdk.widget

import net.activelook.sdk.screen.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class CircleWidgetTest {

    @Test
    fun `init CircleWidget`() {
        var circleWidget =
            CircleWidget(15, 16, 10, true, Color("#151413"))

        assertEquals(15, circleWidget.x)
        assertEquals(16, circleWidget.y)
        assertEquals(10, circleWidget.radius)
        assertEquals(true, circleWidget.isFilled)
        assertEquals(Color("#151413"), circleWidget.color)
        assertEquals(0, circleWidget.paddingLeft)
        assertEquals(0, circleWidget.paddingTop)
        assertEquals(0, circleWidget.paddingRight)
        assertEquals(0, circleWidget.paddingBottom)

        circleWidget = CircleWidget(42, 24, 30, false)

        assertEquals(42, circleWidget.x)
        assertEquals(24, circleWidget.y)
        assertEquals(30, circleWidget.radius)
        assertEquals(false, circleWidget.isFilled)
        assertEquals(null, circleWidget.color)
        assertEquals(0, circleWidget.paddingLeft)
        assertEquals(0, circleWidget.paddingTop)
        assertEquals(0, circleWidget.paddingRight)
        assertEquals(0, circleWidget.paddingBottom)

        circleWidget = CircleWidget(4, 8, 80)
        circleWidget.setPadding(4, 8, 15, 16)

        assertEquals(4, circleWidget.x)
        assertEquals(8, circleWidget.y)
        assertEquals(80, circleWidget.radius)
        assertEquals(true, circleWidget.isFilled)
        assertEquals(null, circleWidget.color)
        assertEquals(4, circleWidget.paddingLeft)
        assertEquals(8, circleWidget.paddingTop)
        assertEquals(15, circleWidget.paddingRight)
        assertEquals(16, circleWidget.paddingBottom)
    }
}