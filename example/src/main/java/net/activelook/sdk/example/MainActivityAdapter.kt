package net.activelook.sdk.example

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_device.view.*

class MainActivityAdapter(private val clickListener: (RecyclerView.ViewHolder)->Unit): ListAdapter<BluetoothDevice, MainActivityAdapter.DeviceListViewHolder>(ItemCallback()) {

    public override fun getItem(position: Int): BluetoothDevice {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_device, parent, false)
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