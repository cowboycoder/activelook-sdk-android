package net.activelook.sdk.scanner

import android.bluetooth.BluetoothAdapter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import net.activelook.sdk.ActiveLookSdk

/**
 * This is a [BluetoothScannerCompat] implementation that uses the API 21
 * [android.bluetooth.le.BluetoothLeScanner] class to perform BLE scanning
 * @see [android.bluetooth.le.BluetoothLeScanner]
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class BluetoothScanner(private val adapter: BluetoothAdapter, sdkCallback: ActiveLookSdk.ScanningCallback) {

    private val callback = ActiveLookScanCallback(sdkCallback)

    @RequiresPermission("android.permission.BLUETOOTH_ADMIN")
    fun startScanning() {

        // NOTE: this only works if advertisement data contains service UUIDs (ScanResult.scanRecord.serviceUuids)
//        val batteryServiceFilter = ScanFilter.Builder()
//                .setServiceUuid(ParcelUuid(BaseUUID(UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")).value))
//                .build()
//
//        val settings = ScanSettings.Builder()
//            .build()
//
//        adapter.bluetoothLeScanner.startScan(listOf(batteryServiceFilter), settings, callback)
        adapter.bluetoothLeScanner.startScan(callback)
    }

    @RequiresPermission("android.permission.BLUETOOTH_ADMIN")
    fun stopScanning() {
        adapter.bluetoothLeScanner.stopScan(callback)
    }
}