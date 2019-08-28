package net.activelook.sdk.operation

import net.activelook.sdk.command.ActiveLookCommand
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ActiveLookOperationTest {

    @Test
    fun displayOff() {
        val operation = ActiveLookOperation.Display(false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Power(false),
            ActiveLookCommand.Clear
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun displayOn() {
        val operation = ActiveLookOperation.Display(true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Power(true)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun clearScreen() {
        val operation = ActiveLookOperation.ClearScreen
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Clear
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun setLedOff() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetLed(false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Led(false)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun setLedOn() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetLed(true)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.Led(true)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `luminosity at 0 with auto adjust at false`() {
        val operation: ActiveLookOperation = ActiveLookOperation.SetBrightness(0, false)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.AmbientLightSensor(false),
            ActiveLookCommand.Luminosity(0)
        )

        assertArrayEquals(expectedList, commands)
    }

}