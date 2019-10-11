package net.activelook.sdk.example

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.session.GattClosedReason

/**
 * This activity inits the SDK and scans for ActiveLook devices
 * It handles the Bluetooth / location permission requests
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_ENABLE_BT = 9000
        private const val REQUEST_ACCESS_COARSE_LOCATION = 9001
    }

    private val sdkInstance = ActiveLookSdk.getInstance(this)

    private val scanCallback = object: ActiveLookSdk.ScanningCallback {

        override fun foundDevices(devices: List<BluetoothDevice>) {
            val new = devices.filter { !deviceList.contains(it) }.toMutableList()
            new.addAll(deviceList)
            adapter.submitList(new)
            deviceList = new
        }

        override fun scanError(err: ActiveLookSdk.ScanningCallback.Error.ScanFailed) {
            Toast.makeText(this@MainActivity, "ERROR: ${err.reason}", Toast.LENGTH_LONG).show()
        }
    }

    private val itemClickListener: (RecyclerView.ViewHolder)->Unit = {
        sdkInstance.stopScanning()
        if(lastClickedDevice != null) {
            sdkInstance.disconnect(lastClickedDevice!!)
        }
        lastClickedDevice = adapter.getItem(it.adapterPosition)
        sdkInstance.connect(this, lastClickedDevice!!)
    }

    private var lastClickedDevice: BluetoothDevice? = null
    private val adapter: MainActivityAdapter = MainActivityAdapter(itemClickListener)
    private var deviceList = listOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.adapter = adapter

//        image.setOnClickListener {
//            val drawable = image.drawable as BitmapDrawable
//            image.setImageBitmap(drawable.bitmap.toGrayscale(16))
//        }

        sdkInstance.connectionListener = object : ActiveLookSdk.ConnectionListener {
            override fun activeLookConnectionEstablished() {
                val intent = Intent(this@MainActivity, OperationsActivity::class.java)
                startActivity(intent)
            }

            override fun activeLookConnectionTerminated(reason: GattClosedReason) {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleManager.adapter ?: return
        if(bleManager.adapter.isEnabled) {
            startScanning()
        } else {
            val enabledBt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enabledBt, REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                startScanning()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sdkInstance.stopScanning()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(lastClickedDevice != null) {
            sdkInstance.disconnect(lastClickedDevice!!)
        }
    }

    // region Scanning

    private fun startScanning() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_ACCESS_COARSE_LOCATION)
        } else {
            sdkInstance.startScanning(scanCallback)
        }
    }

    // endregion Scanning

    // region Permissions

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if(grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            }
        }
    }

    // endregion Permissions
}