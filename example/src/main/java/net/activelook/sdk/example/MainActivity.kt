package net.activelook.sdk.example

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item_device.view.*
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.session.GattClosedReason
import net.activelook.sdk.util.toGrayscale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_ENABLE_BT = 9000
        private const val REQUEST_ACCESS_COARSE_LOCATION = 9001
    }

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
        ActiveLookSdk.shared.stopScanning()
        if(lastClickedDevice != null) {
            ActiveLookSdk.shared.disconnect(lastClickedDevice!!)
        }
        lastClickedDevice = adapter.getItem(it.adapterPosition)
        ActiveLookSdk.shared.connect(this, lastClickedDevice!!)
    }

    private var lastClickedDevice: BluetoothDevice? = null
    private val adapter: DeviceListAdapter = DeviceListAdapter(itemClickListener)
    private var deviceList = listOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.adapter = adapter

        image.setOnClickListener {
            val drawable = image.drawable as BitmapDrawable
            image.setImageBitmap(drawable.bitmap.toGrayscale(16))
        }

        ActiveLookSdk.shared.connectionListener = object: ActiveLookSdk.ConnectionListener {
            override fun activeLookConnectionEstablished() {
                ActiveLookSdk.shared.sayHello()
                ActiveLookSdk.shared.getBattery()
            }

            override fun activeLookConnectionTerminated(reason: GattClosedReason) {
                // TODO: ?
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
        ActiveLookSdk.shared.stopScanning()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(lastClickedDevice != null) {
            ActiveLookSdk.shared.disconnect(lastClickedDevice!!)
        }
    }

    // region Scanning

    private fun startScanning() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_ACCESS_COARSE_LOCATION)
        } else {
            ActiveLookSdk.shared.startScanning(scanCallback)
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

class DeviceListAdapter(private val clickListener: (RecyclerView.ViewHolder)->Unit): ListAdapter<BluetoothDevice, DeviceListAdapter.DeviceListViewHolder>(ItemCallback()) {

    public override fun getItem(position: Int): BluetoothDevice {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_device, parent, false)
        val vh = DeviceListViewHolder(view)
        view.setOnClickListener { clickListener(vh) }
        return vh
    }

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        val device = getItem(position)
        holder.bindDevice(device)
    }

    class DeviceListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindDevice(device: BluetoothDevice) {
            itemView.nameLabel.text = device.name
            itemView.uuidLabel.text = device.address
        }
    }

    class ItemCallback: DiffUtil.ItemCallback<BluetoothDevice>() {

        override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return true
        }
    }
}
