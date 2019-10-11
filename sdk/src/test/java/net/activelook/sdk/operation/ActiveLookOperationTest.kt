package net.activelook.sdk.operation

import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.screen.Screen
import org.junit.Assert.*
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

    @Test
    fun `add screen`() {
        val screen = Screen.Builder(1)
            .build()
        val operation: ActiveLookOperation = ActiveLookOperation.AddScreen(screen)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.SaveLayout(screen.mapToLayout(10, 10))
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `delete screen`() {
        val operation: ActiveLookOperation = ActiveLookOperation.DeleteScreen(15)
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.EraseLayout(15)
        )

        assertArrayEquals(expectedList, commands)
    }

    @Test
    fun `delete all screens`() {
        val operation: ActiveLookOperation = ActiveLookOperation.DeleteAllScreens()
        val commands = operation.commands

        assertEquals(50, commands.size)
        val eraseLayoutCommands = commands.filterIsInstance<ActiveLookCommand.EraseLayout>()
        assertEquals(50, eraseLayoutCommands.size)
        assertEquals(50, eraseLayoutCommands.map { it.layoutId }.distinct().size)
        assertTrue(eraseLayoutCommands.map { it.layoutId }.containsAll(IntRange(10, 59).toList()))
    }

    @Test
    fun `display screen`() {
        val operation: ActiveLookOperation = ActiveLookOperation.DisplayScreen(15, "Test")
        val commands = operation.commands

        val expectedList = arrayOf(
            ActiveLookCommand.DisplayLayout(15, "Test")
        )

        assertArrayEquals(expectedList, commands)
    }
}