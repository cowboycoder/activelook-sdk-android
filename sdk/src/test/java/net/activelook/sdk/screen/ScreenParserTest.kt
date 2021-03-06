package net.activelook.sdk.screen

import net.activelook.sdk.exception.JsonInvalidException
import net.activelook.sdk.exception.JsonVersionInvalidException
import net.activelook.sdk.widget.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

class ScreenParserTest {

    @Test
    fun generateScreenFromJson() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |        {
            |            "type": "text",
            |            "position": {
            |                "x": 14,
            |                "y": 34
            |            },
            |            "orientation": 0,
            |            "color": "#112233",
            |            "font": "ActiveLook",
            |            "size": 13,
            |            "value": "km/h"
            |        }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val textWidget: Widget =
            TextWidget(14, 34, "km/h", color = Color("#112233"))

        assertThat(screen.widgets, `is`(listOf(textWidget)))
    }

    @Test
    fun generateScreenFromJsonWithWrongId() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "150",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |        {
            |            "type": "text",
            |            "position": {
            |                "x": 14,
            |                "y": 34
            |            },
            |            "orientation": 0,
            |            "color": "#112233",
            |            "font": "ActiveLook",
            |            "size": 13,
            |            "value": "km/h"
            |        }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(59, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val textWidget: Widget =
            TextWidget(14, 34, "km/h", color = Color("#112233"))

        assertThat(screen.widgets, `is`(listOf(textWidget)))
    }

    @Test(expected = JsonVersionInvalidException::class)
    fun generateScreenFromJsonWithWrongVersion() {
        val json = """
            |{
            |    "version": 3,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |        {
            |            "type": "text",
            |            "position": {
            |                "x": 14,
            |                "y": 34
            |            },
            |            "orientation": 0,
            |            "color": "#112233",
            |            "font": "ActiveLook",
            |            "size": 13,
            |            "value": "km/h"
            |        }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithoutPadding() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "widgets": [
            |        {
            |            "type": "text",
            |            "position": {
            |                "x": 14,
            |                "y": 34
            |            },
            |            "orientation": 0,
            |            "color": "#112233",
            |            "font": "ActiveLook",
            |            "size": 13,
            |            "value": "km/h"
            |        }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithoutId() {
        val json = """
            |{
            |    "version": 1,
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |        {
            |            "type": "text",
            |            "position": {
            |                "x": 14,
            |                "y": 34
            |            },
            |            "orientation": 0,
            |            "color": "#112233",
            |            "font": "ActiveLook",
            |            "size": 13,
            |            "value": "km/h"
            |        }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test
    fun generateScreenFromJsonWithoutWidget() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    }
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        assertThat(screen.widgets, `is`(emptyList()))
    }

    @Test
    fun generateScreenFromJsonWithEmptyWidgets() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": []
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        assertThat(screen.widgets, `is`(emptyList()))
    }

    @Test
    fun generateScreenFromJsonWithCircleWidget() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "circle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "radius": 25,
            |           "color": "#32f3e1",
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val circleWidget: Widget = CircleWidget(36, 13, 25, true, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(circleWidget)))
    }

    @Test
    fun generateScreenFromJsonWithCircleWidgetWithoutStyle() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "circle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "radius": 25,
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val circleWidget: Widget = CircleWidget(36, 13, 25, true, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(circleWidget)))
    }

    @Test
    fun generateScreenFromJsonWithCircleWidgetWithoutColor() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "circle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "radius": 25,
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val circleWidget: Widget = CircleWidget(36, 13, 25, true)

        assertThat(screen.widgets, `is`(listOf(circleWidget)))
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithCircleWidgetWithoutRadius() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "circle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "color": "#32f3e1",
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test
    fun generateScreenFromJsonWithCircleWidgetWithOutlineStyle() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "circle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "radius": 25,
            |           "color": "#32f3e1",
            |           "style": "outline"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val circleWidget: Widget = CircleWidget(36, 13, 25, false, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(circleWidget)))
    }

    @Test
    fun generateScreenFromJsonWithLineWidget() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "line",
            |           "start": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "end": {
            |               "x": 42,
            |               "y": 12
            |           },
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = LineWidget(36, 13, 42, 12, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test
    fun generateScreenFromJsonWithLineWidgetWithoutColor() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "line",
            |           "start": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "end": {
            |               "x": 42,
            |               "y": 12
            |           }
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = LineWidget(36, 13, 42, 12)

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithLineWidgetWithoutStartPoint() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "line",
            |           "end": {
            |               "x": 42,
            |               "y": 12
            |           },
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithLineWidgetWithoutEndPoint() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "line",
            |           "start": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test
    fun generateScreenFromJsonWithPointWidget() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "point",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = PointWidget(36, 13, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test
    fun generateScreenFromJsonWithPointWidgetWithoutColor() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "point",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           }
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = PointWidget(36, 13)

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithPointWidgetWithoutPosition() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "point",
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test
    fun generateScreenFromJsonWithRectangleWidget() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "height": 36,
            |           "width": 13,
            |           "color": "#32f3e1",
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = RectangleWidget(36, 13, 36, 13, true, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test
    fun generateScreenFromJsonWithRectangleWidgetWithOutlineStyle() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "height": 36,
            |           "width": 13,
            |           "color": "#32f3e1",
            |           "style": "outline"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = RectangleWidget(36, 13, 36, 13, false, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test
    fun generateScreenFromJsonWithRectangleWidgetWithoutStyle() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "height": 36,
            |           "width": 13,
            |           "color": "#32f3e1"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = RectangleWidget(36, 13, 36, 13, true, Color("#32f3e1"))

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test
    fun generateScreenFromJsonWithRectangleWidgetWithoutColor() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "height": 36,
            |           "width": 13,
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        val screen = Screen.Builder(json).build()

        assertEquals(10, screen.id)
        assertEquals(12, screen.x0)
        assertEquals(5, screen.y0)
        assertEquals(291, screen.x1)
        assertEquals(250, screen.y1)

        val widget: Widget = RectangleWidget(36, 13, 36, 13, true, null)

        assertThat(screen.widgets, `is`(listOf(widget)))
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithRectangleWidgetWithoutWidth() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "height": 36,
            |           "color": "#32f3e1",
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithRectangleWidgetWithoutHeight() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "position": {
            |               "x": 36,
            |               "y": 13
            |           },
            |           "width": 13,
            |           "color": "#32f3e1",
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithRectangleWidgetWithoutPosition() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "1",
            |    "padding": {
            |        "left": 12,
            |        "right": 12,
            |        "top": 5,
            |        "bottom": 5
            |    },
            |    "widgets": [
            |       {
            |           "type": "rectangle",
            |           "height": 36,
            |           "width": 13,
            |           "color": "#32f3e1",
            |           "style": "filled"
            |       }
            |    ]
            |}"""
            .trimMargin()

        Screen.Builder(json).build()
    }
}