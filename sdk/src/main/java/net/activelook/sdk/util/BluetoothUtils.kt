package net.activelook.sdk.util

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.os.Build

internal fun BluetoothDevice.connectGattCompat(
    unused: Context,
    autoConnect: Boolean,
    callback: BluetoothGattCallback
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectGatt(unused, autoConnect, callback, BluetoothDevice.TRANSPORT_LE)
    } else {
        connectGatt(unused, autoConnect, callback)
    }
}