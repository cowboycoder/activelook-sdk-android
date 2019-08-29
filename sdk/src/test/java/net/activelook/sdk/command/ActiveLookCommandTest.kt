package net.activelook.sdk.command

import org.junit.Assert.assertEquals
import org.junit.Test

class ActiveLookCommandTest {

    @Test
    fun `power off`() {
        val command = ActiveLookCommand.Power(false)
        assertEquals("power off", command.command)
    }

    @Test
    fun `power on`() {
        val command = ActiveLookCommand.Power(true)
        assertEquals("power on", command.command)
    }

    @Test
    fun clear() {
        val command = ActiveLookCommand.Clear
        assertEquals("clear", command.command)
    }

    @Test
    fun `led off`() {
        val command = ActiveLookCommand.Led(false)
        assertEquals("led off", command.command)
    }

    @Test
    fun `led on`() {
        val command = ActiveLookCommand.Led(true)
        assertEquals("led on", command.command)
    }

    @Test
    fun `luminosity with minimum value`() {
        val command = ActiveLookCommand.Luminosity(0)
        assertEquals("luma 0", command.command)
    }

    @Test
    fun `luminosity with maximum value`() {
        val command = ActiveLookCommand.Luminosity(15)
        assertEquals("luma 15", command.command)
    }

    @Test
    fun `luminosity with a value which exceeds max`() {
        val command = ActiveLookCommand.Luminosity(16)
        assertEquals("luma 15", command.command)
    }

    @Test
    fun `luminosity with a value which exceeds min`() {
        val command = ActiveLookCommand.Luminosity(-1)
        assertEquals("luma 0", command.command)
    }

    @Test
    fun `ambient light sensor off`() {
        val command = ActiveLookCommand.AmbientLightSensor(false)
        assertEquals("als off", command.command)
    }

    @Test
    fun `ambient light sensor on`() {
        val command = ActiveLookCommand.AmbientLightSensor(true)
        assertEquals("als on", command.command)
    }

}