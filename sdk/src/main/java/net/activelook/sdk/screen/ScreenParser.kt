package net.activelook.sdk.screen

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import net.activelook.sdk.Font
import net.activelook.sdk.exception.JsonInvalidException
import net.activelook.sdk.exception.JsonVersionInvalidException
import net.activelook.sdk.layout.LayoutWidget
import net.activelook.sdk.util.Point
import net.activelook.sdk.widget.*
import java.lang.IllegalArgumentException

internal object ScreenParser {

    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(WidgetJson::class.java, "type")
                .withSubtype(WidgetJson.TextWidgetJson::class.java, WidgetType.text.name)
                .withSubtype(WidgetJson.CircleWidgetJson::class.java, WidgetType.circle.name)
                .withSubtype(WidgetJson.LineWidgetJson::class.java, WidgetType.line.name)
                .withSubtype(WidgetJson.PointWidgetJson::class.java, WidgetType.point.name)
                .withSubtype(WidgetJson.RectangleWidgetJson::class.java, WidgetType.rectangle.name)
        )
        .build()

    fun parse(rawJsonContent: String): Screen.Builder {
        val adapter = moshi.adapter(ScreenBaseJson::class.java)
        val screenJson = adapter.fromJson(rawJsonContent)

        return when (screenJson?.version) {
            1 -> parseV1(rawJsonContent)
            else -> throw JsonVersionInvalidException()
        }

    }

    private fun parseV1(rawJsonContent: String): Screen.Builder {
        val adapter = moshi.adapter(ScreenV1Json::class.java)

        val screenJson = try {
            adapter.fromJson(rawJsonContent) ?: throw JsonInvalidException()
        } catch (e: JsonDataException) {
            throw JsonInvalidException()
        }

        val id = screenJson.id
        val paddingObject = screenJson.padding
        val paddingLeft = paddingObject.left
        val paddingTop = paddingObject.top
        val paddingRight = paddingObject.right
        val paddingBottom = paddingObject.bottom

        val widgets = screenJson.widgets.map { it.mapToModel() }

        val builder = Screen.Builder(id)
            .setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        screenJson.textOrigin?.let { origin ->
            screenJson.textOrientation?.let { orientation ->
                builder.setText(Point(origin.x, origin.y), orientation, true)
            }
        }

        screenJson.foregroundColor?.let { builder.setForegroundColor(it) }
        screenJson.backgroundColor?.let { builder.setBackgroundColor(it) }
        screenJson.font?.let { builder.setFont(it) }

        for (widget in widgets) {
            builder.addWidget(widget)
        }

        return builder
    }

    internal open class ScreenBaseJson(
        val version: Int
    )

    @JsonClass(generateAdapter = true)
    internal class ScreenV1Json(
        version: Int,
        val id: Int,
        val padding: PaddingJson,
        val textOrigin: PositionJson?,
        val textOrientation: Orientation?,
        val foregroundColor: Int?,
        val backgroundColor: Int?,
        val font: Font?,
        val widgets: List<WidgetJson> = emptyList()
    ) : ScreenBaseJson(version)

    internal class PaddingJson(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    )

    internal sealed class WidgetJson(val type: WidgetType) {

        abstract fun mapToModel(): Widget

        @JsonClass(generateAdapter = true)
        internal class TextWidgetJson(
            val position: PositionJson,
            var color: String?,
            var font: String?,
            val value: String
        ) : WidgetJson(WidgetType.text) {

            override fun mapToModel(): Widget {
                return TextWidget(
                    this.position.x,
                    this.position.y,
                    this.value,
                    font = findFont(font),
                    color = makeColor(color)
                )
            }

            private fun findFont(font: String?) : Font? {
                if(font == null) {
                    return null
                }
                try {
                    return Font.valueOf(font)
                } catch (e: IllegalArgumentException) { }
                return Font.findFont(font)
            }

            private fun makeColor(color: String?) : Color? {
                if(color == null) {
                    return null
                }
                return Color(color)
            }
        }

        @JsonClass(generateAdapter = true)
        internal class CircleWidgetJson(
            val position: PositionJson,
            val radius: Int,
            val color: String? = null,
            val style: Style? = Style.filled
        ) : WidgetJson(WidgetType.text) {

            override fun mapToModel(): Widget {
                return CircleWidget(
                    this.position.x,
                    this.position.y,
                    this.radius,
                    color = color?.let { Color(it) },
                    isFilled = style != Style.outline
                )
            }
        }

        @JsonClass(generateAdapter = true)
        internal class LineWidgetJson(
            val start: PositionJson,
            val end: PositionJson,
            val color: String? = null
        ) : WidgetJson(WidgetType.text) {

            override fun mapToModel(): Widget {
                return LineWidget(
                    this.start.x,
                    this.start.y,
                    this.end.x,
                    this.end.y,
                    color = color?.let { Color(it) }
                )
            }
        }

        @JsonClass(generateAdapter = true)
        internal class PointWidgetJson(
            val position: PositionJson,
            val color: String? = null
        ) : WidgetJson(WidgetType.text) {

            override fun mapToModel(): Widget {
                return PointWidget(
                    this.position.x,
                    this.position.y,
                    color = color?.let { Color(it) }
                )
            }
        }

        @JsonClass(generateAdapter = true)
        internal class RectangleWidgetJson(
            val position: PositionJson,
            val height: Int,
            val width: Int,
            val color: String? = null,
            val style: Style? = Style.filled
        ) : WidgetJson(WidgetType.text) {

            override fun mapToModel(): Widget {
                return RectangleWidget(
                    this.position.x,
                    this.position.y,
                    this.height,
                    this.width,
                    isFilled = style != Style.outline,
                    color = color?.let { Color(it) }
                )
            }
        }
    }

    internal class PositionJson(
        val x: Int,
        val y: Int
    )

    internal enum class WidgetType {
        text,
        circle,
        line,
        point,
        rectangle,
        image
    }

    internal enum class Style {
        filled,
        outline
    }

}