package net.activelook.sdk.command

import org.junit.Assert.assertEquals
import org.junit.Test

class ActiveLookCommandTest {

    @Test
    fun powerOff() {
        val command = ActiveLookCommand.Power(false)
        assertEquals("power off", command.command)
    }

    @Test
    fun powerOn() {
        val command = ActiveLookCommand.Power(true)
        assertEquals("power on", command.command)
    }

}