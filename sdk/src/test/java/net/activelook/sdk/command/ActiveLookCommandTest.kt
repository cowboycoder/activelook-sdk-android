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

    @Test
    fun clear() {
        val command = ActiveLookCommand.Clear
        assertEquals("clear", command.command)
    }

    @Test
    fun ledOff() {
        val command = ActiveLookCommand.Led(false)
        assertEquals("led off", command.command)
    }

    @Test
    fun ledOn() {
        val command = ActiveLookCommand.Led(true)
        assertEquals("led on", command.command)
    }

    @Test
    fun luminosityMin() {
        val command = ActiveLookCommand.Luminosity(0)
        assertEquals("luma 0", command.command)
    }

    @Test
    fun luminosityMax() {
        val command = ActiveLookCommand.Luminosity(15)
        assertEquals("luma 15", command.command)
    }

    @Test
    fun luminosityWrongValue() {
        val command = ActiveLookCommand.Luminosity(100)
        assertEquals("luma 100", command.command)
    }

    @Test
    fun ambientLightSensorOff() {
        val command = ActiveLookCommand.AmbientLightSensor(false)
        assertEquals("als off", command.command)
    }

    @Test
    fun ambientLightSensorOn() {
        val command = ActiveLookCommand.AmbientLightSensor(true)
        assertEquals("als on", command.command)
    }

}