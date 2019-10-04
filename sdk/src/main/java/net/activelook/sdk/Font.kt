package net.activelook.sdk

import com.squareup.moshi.Json

enum class Font(internal val value: Int) {
    @Json(name = "small")
    SMALL(1),
    @Json(name = "medium")
    MEDIUM(2),
    @Json(name = "large")
    LARGE(3)
}