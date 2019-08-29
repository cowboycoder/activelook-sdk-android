package net.activelook.sdk.example

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import net.activelook.sdk.ActiveLookSdk

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        val bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        ActiveLookSdk.setup(bleManager)
    }

}