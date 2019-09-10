package net.activelook.sdk.blemodel

import java.util.*

internal sealed class Descriptor(private val baseUuid: BaseUUID) {

    val uuid: UUID
        get() = baseUuid.value

    object ClientCharacteristicConfiguration: Descriptor(BaseUUID(0x2902))
}