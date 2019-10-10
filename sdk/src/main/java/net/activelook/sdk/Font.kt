package net.activelook.sdk

import com.squareup.moshi.Json

/**
 * The SDK supports currently three fonts
 */
enum class Font(internal val value: Int, internal val size: Int) {
    @Json(name = "small")
    SMALL(1, 28),
    @Json(name = "medium")
    MEDIUM(2, 48),
    @Json(name = "large")
    LARGE(3, 72)
}