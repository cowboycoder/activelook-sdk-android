package net.activelook.sdk.blemodel

import java.util.*

internal sealed class Service(private val baseUuid: BaseUUID) {

    val uuid: UUID
        get() = baseUuid.value

    object GenericAccess : Service(BaseUUID(0x1800))
    object DeviceInformation : Service(BaseUUID(0x180A))
    object Battery : Service(BaseUUID(0x180F))
    object Suota : Service(BaseUUID(0xFEF5))
    object CommandInterface :
        Service(BaseUUID(UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb7")))

}