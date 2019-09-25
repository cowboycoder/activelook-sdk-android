package net.activelook.sdk.layout

import net.activelook.sdk.layout.LayoutWidget.Companion.ID_CIRCLE_FILLED
import net.activelook.sdk.layout.LayoutWidget.Companion.ID_CIRCLE_OUTLINE
import net.activelook.sdk.layout.LayoutWidget.Companion.ID_COLOR
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.toHex


internal class Layout(
    val id: Int,
    val x0: Int,
    val y0: Int,
    val x1: Int,
    val y1: Int,
    val foregroundColor: Int,
    val backgroundColor: Int,
    val font: Int,
    val textValid: Boolean,
    val textX0: Int,
    val textY0: Int,
    val textRotation: Int,
    val textOpacity: Boolean,
    val additionalCommands: List<LayoutWidget>
) {

    companion object {
        internal const val SIZE_ADDITIONAL_COMMANDS_MAX = 127 - 17
    }

    fun mapToCommand(): String {
        var sizeAdditionalCommands = 0
        val additionalCommandsToAdd = mutableListOf<LayoutWidget>()
        for (additionalCommand in additionalCommands) {
            if (sizeAdditionalCommands + additionalCommand.getCommandSize() > SIZE_ADDITIONAL_COMMANDS_MAX) {
                break
            }
            additionalCommandsToAdd += additionalCommand
            sizeAdditionalCommands += additionalCommand.getCommandSize()
        }

        return "${id.toHex()}${sizeAdditionalCommands.toHex()}" +
                "${x0.toHex(4)}${y0.toHex()}${x1.toHex(4)}${y1.toHex()}" +
                "${foregroundColor.toHex()}${backgroundColor.toHex()}${font.toHex()}" +
                "${textValid.toHex()}${textX0.toHex(4)}${textY0.toHex()}" +
                "${textRotation.toHex()}${textOpacity.toHex()}" +
                additionalCommandsToAdd.joinToString(separator = "") { it.mapToCommand() }
    }

}

internal interface LayoutWidget {
    fun mapToCommand(): String

    fun getCommandSize(): Int {
        return mapToCommand().length / 2
    }

    companion object {
        const val ID_BITMAP = 0
        const val ID_CIRCLE_OUTLINE = 1
        const val ID_CIRCLE_FILLED = 2
        const val ID_COLOR = 3
        const val ID_FONT = 4
        const val ID_LINE = 5
        const val ID_POINT = 6
        const val ID_RECTANGLE_OUTLINE = 7
        const val ID_RECTANGLE_FILLED = 8
        const val ID_TEXT = 9
    }
}

class Color(
    val color: Int
) : LayoutWidget {

    override fun mapToCommand(): String {
        return "${ID_COLOR.toHex()}${color.toHex()}"
    }

}

class Circle(
    private val isFilled: Boolean,
    private val x0: Int,
    private val y0: Int,
    private val radius: Int
) : LayoutWidget {

    override fun mapToCommand(): String {
        val x = Screen.MAX_WIDTH - Screen.PADDING_LEFT - this.x0
        val y = Screen.MAX_HEIGHT - Screen.PADDING_TOP - this.y0

        val circleId = if (isFilled) {
            ID_CIRCLE_FILLED
        } else {
            ID_CIRCLE_OUTLINE
        }

        return "${circleId.toHex()}${x.toHex(4)}${y.toHex(4)}${radius.toHex(4)}"
    }

}