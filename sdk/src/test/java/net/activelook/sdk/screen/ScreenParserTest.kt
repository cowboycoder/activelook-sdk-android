package net.activelook.sdk.screen

import net.activelook.sdk.exception.JsonInvalidException
import net.activelook.sdk.exception.JsonVersionInvalidException
import net.activelook.sdk.screen.Screen
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
            |    "id": "10",
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

        val textWidget = TextWidget(14, 34, "km/h", color = Color("#112233"))
        assertThat(listOf(textWidget), `is`(screen.widgets))
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

        val textWidget = TextWidget(14, 34, "km/h", color = Color("#112233"))
        assertThat(listOf(textWidget), `is`(screen.widgets))
    }

    @Test(expected = JsonVersionInvalidException::class)
    fun generateScreenFromJsonWithWrongVersion() {
        val json = """
            |{
            |    "version": 3,
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

        Screen.Builder(json).build()
    }

    @Test(expected = JsonInvalidException::class)
    fun generateScreenFromJsonWithoutPadding() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "150",
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
            |    "id": "10",
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

        assertThat(emptyList(), `is`(screen.widgets))
    }

    @Test
    fun generateScreenFromJsonWithEmptyWidgets() {
        val json = """
            |{
            |    "version": 1,
            |    "id": "10",
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

        assertThat(emptyList(), `is`(screen.widgets))
    }
}