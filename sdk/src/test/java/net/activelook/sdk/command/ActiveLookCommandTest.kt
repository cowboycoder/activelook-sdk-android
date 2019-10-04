package net.activelook.sdk.command

import net.activelook.sdk.layout.Layout
import net.activelook.sdk.layout.LayoutWidget
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

    @Test
    fun `save layout 10 with no additional command`() {
        val layout = Layout(10, 0, 0, 303, 255, 15, 0, 3, true, 79, 127, 4, true, emptyList())
        val command = ActiveLookCommand.SaveLayout(layout)
        assertEquals(
            "savelayout 0x0A" +
                    "00" +
                    "0000" +
                    "00" +
                    "012F" +
                    "FF" +
                    "0F" +
                    "00" +
                    "03" +
                    "01" +
                    "00E0" +
                    "80" +
                    "04" +
                    "01", command.command
        )
    }

    @Test
    fun `save layout 13 with additional bitmap and text`() {
        val layout = Layout(
            13,
            75,
            100,
            195,
            217,
            15,
            0,
            3,
            true,
            130,
            67,
            4,
            false,
            listOf(
                LayoutWidget.Font(1),
                LayoutWidget.Bitmap(4, 228, 155),
                LayoutWidget.Text(140, 112, "KM/H")
            )
        )

        val command =
            ActiveLookCommand.SaveLayout(layout)
        assertEquals(
            "savelayout 0x" +
                    "0D" +
                    "12" +
                    "004B" +
                    "64" +
                    "00C3" +
                    "D9" +
                    "0F" +
                    "00" +
                    "03" +
                    "01" +
                    "00AD" +
                    "BC" +
                    "04" +
                    "00" +
                    "0401" +
                    "0004004B0064" +
                    "0900A3008F044B4D2F48",
            command.command
        )
    }

    @Test
    fun `erase layout`() {
        val command =
            ActiveLookCommand.EraseLayout(15)
        assertEquals(
            "eraselayout 15",
            command.command
        )
    }

    @Test
    fun `display layout`() {
        val command =
            ActiveLookCommand.DisplayLayout(15, "Test")
        assertEquals(
            "layout 15 Test",
            command.command
        )
    }

}