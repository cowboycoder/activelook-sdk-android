package net.activelook.sdk.screen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorTest {

    @Test
    fun `init Color`() {
        val color = Color("#453f96")

        assertEquals(69, color.r)
        assertEquals(63, color.g)
        assertEquals(150, color.b)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `init Color without #`() {
        Color("453f96")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `init Color with wrong value`() {
        Color("#453k96")
    }

    @Test
    fun `test equals is symmetric`() {
        val x = Color("#75324D")
        val y = Color("#75324D")

        assertTrue(x == y && y == x)
        assertTrue(x.hashCode() == y.hashCode())
    }
}