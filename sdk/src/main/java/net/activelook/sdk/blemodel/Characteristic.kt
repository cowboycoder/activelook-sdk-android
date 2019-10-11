package net.activelook.sdk.blemodel

import java.util.*

internal sealed class Characteristic(private val baseUuid: BaseUUID) {

    val uuid: UUID
        get() = baseUuid.value

    // region Generic Access Service
    object DeviceName: Characteristic(BaseUUID(0x2A00))                     // read
    object Appearance: Characteristic(BaseUUID(0x2A01))                     // read
    object PreferredConnectionParameters: Characteristic(BaseUUID(0x2A04))  // read
    // endregion Generic Access Service

    // region Device Information Service
    object ManufacturerName: Characteristic(BaseUUID(0x2A29))               // read
    object ModelNumberString: Characteristic(BaseUUID(0x2A24))              // read
    // endregion Generic Access Service

    // region Battery Service
    object BatteryLevel: Characteristic(BaseUUID(0x2A19))                   // read, notify
    // endregion Battery Service

    // region Suota Service
    // TODO: ?
    // endregion Suota Service

    // region Command Interface Service
    object RxServer: Characteristic(BaseUUID(UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cbA")))  // write
    object TxServer: Characteristic(BaseUUID(UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8")))  // read, notify
    object FlowControl :
        Characteristic(BaseUUID(UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb9"))) {  // write, notify
        const val ON = 0x01
        const val OFF = 0x02
    }
    // endregion Command Interface Service
}