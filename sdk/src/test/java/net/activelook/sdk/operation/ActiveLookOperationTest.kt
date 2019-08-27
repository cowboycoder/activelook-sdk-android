package net.activelook.sdk.operation

import net.activelook.sdk.command.ActiveLookCommand
import org.junit.Assert.*
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

}