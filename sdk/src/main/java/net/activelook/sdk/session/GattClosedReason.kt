package net.activelook.sdk.session

import android.bluetooth.BluetoothGatt

sealed class GattClosedReason(val rawValue: Int) {

    object Success: GattClosedReason(BluetoothGatt.GATT_SUCCESS)
    object DeviceDisconnect: GattClosedReason(0x13)
    object ConnectionTimeout: GattClosedReason(0x08)
    object GattError: GattClosedReason(0x85)
    class Unknown(rawValue: Int): GattClosedReason(rawValue)

    // TODO: add all codes?
    // https://android.googlesource.com/platform/external/bluetooth/bluedroid/+/5738f83aeb59361a0a2eda2460113f6dc9194271/stack/include/gatt_api.h
}