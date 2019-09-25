package net.activelook.sdk.util

internal fun String.toHex(length: Int = 2) =
    this.toByteArray().joinToString("") { "%0${length}X".format(it) }

internal fun Int.toHex(length: Int = 2): String {
    return "%0${length}X".format(this)
}

internal fun Boolean.toHex(length: Int = 2): String {
    return if (this) {
        1.toHex(length)
    } else {
        0.toHex(length)
    }
}