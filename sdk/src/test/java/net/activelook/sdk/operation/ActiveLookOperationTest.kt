package net.activelook.sdk.operation

import net.activelook.sdk.command.ActiveLookCommand
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ActiveLookOperationTest {

    @Test
    fun `display off`() {
        val operation = ActiveLookOperation.Display(false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Power(false),
            ActiveLookCommand.Clear
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `display on`() {
        val operation = ActiveLookOperation.Display(true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Power(true)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `clear screen`() {
        val operation = ActiveLookOperation.ClearScreen
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Clear
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set led off`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetLed(false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Led(false)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set led on`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetLed(true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Led(true)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set brightness with luminosity at 0 and auto adjust at false`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetBrightness(0, false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.AmbientLightSensor(false),
            ActiveLookCommand.Luminosity(0)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set brightness with luminosity at 0 and auto adjust at true`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetBrightness(0, true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.AmbientLightSensor(true),
            ActiveLookCommand.Luminosity(0)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set brightness with luminosity at 15 and auto adjust at false`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetBrightness(15, false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.AmbientLightSensor(false),
            ActiveLookCommand.Luminosity(15)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set brightness with luminosity at 15 and auto adjust at true`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetBrightness(15, true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.AmbientLightSensor(true),
            ActiveLookCommand.Luminosity(15)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `set brightness with luminosity at 100 and auto adjust at true`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetBrightness(100, true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.AmbientLightSensor(true),
            ActiveLookCommand.Luminosity(15)
        )

        assertArrayEquals(expectedList, commands)
    }
}