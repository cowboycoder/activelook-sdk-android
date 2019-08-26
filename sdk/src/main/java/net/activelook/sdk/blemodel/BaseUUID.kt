package net.activelook.sdk.blemodel

import java.util.*

/**
 * This will tak a base UUID (of 16- or 32-bits) and convert it to an acceptable 128-bit UUID
 * as defined in the Bluetooth 4.0 specification
 */
internal class BaseUUID {

    val value: UUID
    val uuidString: String
        get() = value.toString()

    constructor(int: Int) {
        val formatted = String.format("%08X", int)
        value = UUID.fromString("$formatted-0000-1000-8000-00805F9B34FB")
    }

    constructor(short: Short): this(short.toInt())

    constructor(uuid: UUID) {
        this.value = uuid
    }
}