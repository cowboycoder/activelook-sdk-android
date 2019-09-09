package net.activelook.sdk.session

import android.bluetooth.*
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresPermission
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.command.ActiveLookCommandFragment
import java.util.concurrent.CountDownLatch

// TODO: need handlers to communicate messages for callbacks
// TODO: ideas -> https://medium.com/@martijn.van.welie/making-android-ble-work-part-3-117d3a8aee23
// TODO: need to find a solution for concatenating commands with `;`?

internal class GattSession(val device: BluetoothDevice, private val sessionHandler: Handler, private val notificationHandler: Handler): BluetoothGattCallback() {

    /**
     * Communicates session events to the [sessionHandler]
     */
    sealed class Event {
        data class Established(val session: GattSession): Event()
        data class Closed(val session: GattSession, val reason: GattClosedReason): Event()
    }

    // region Private Properties

    private var gatt: BluetoothGatt? = null
    private var writeLatch: CountDownLatch? = null
    private var currentWriteResult: Int? = null

    // endregion Private Properties

    // region Internal Methods

    /**
     * Write an [ActiveLookCommandFragment] to the BLE device
     */
    fun writeCommandFragment(fragment: ActiveLookCommandFragment): Int {
        val l = CountDownLatch(1)
        write(fragment.data)
        writeLatch = l
        l.await()
        return currentWriteResult ?: -1
    }

    /**
     * Disconnect the session
     */
    fun disconnect() {
        gatt?.disconnect()
    }

    // endregion Internal Methods

    // region BluetoothGattCallback

    @RequiresPermission("android.permission.BLUETOOTH")
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if(status == BluetoothGatt.GATT_SUCCESS) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("TEST", "connected")
                    this.gatt = gatt
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("TEST", "disconnected")
                    gatt.close()
                    cleanup(GattClosedReason.Success)
                }
                BluetoothProfile.STATE_CONNECTING,
                BluetoothProfile.STATE_DISCONNECTING -> {}
            }
        } else {
            Log.d("TEST", "TODO: display error -> $status")
            gatt.close()
            val closedReason: GattClosedReason = when(status) {
                GattClosedReason.ConnectionTimeout.rawValue -> GattClosedReason.ConnectionTimeout
                GattClosedReason.DeviceDisconnect.rawValue -> GattClosedReason.DeviceDisconnect
                GattClosedReason.GattError.rawValue -> GattClosedReason.GattError
                else -> GattClosedReason.Unknown(status)
            }
            cleanup(closedReason)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {

        if(status != BluetoothGatt.GATT_SUCCESS) {
            Log.d("TEST", "Service discovery failed")
            disconnect()
            return
        }

        val event = Event.Established(this)
        val message = sessionHandler.obtainMessage(0, event)
        sessionHandler.sendMessage(message)
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        currentWriteResult = status
        Log.d("TEST", "got characteristic write status: ${status == BluetoothGatt.GATT_SUCCESS}")
        writeLatch?.countDown()
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val value = characteristic.getStringValue(0)
        Log.d("TEST", "got characteristic notification: $value")
        // TODO: signal notification
    }

    // endregion BluetoothGattCallback

    // region Private Methods

    /**
     * When the GattSession is closed we stop our processor threads and send
     * a message to the sessionHandler
     */
    private fun cleanup(closedReason: GattClosedReason) {
        this.gatt = null
        val event = Event.Closed(this, closedReason)
        val message = sessionHandler.obtainMessage(0, event)
        sessionHandler.sendMessage(message)
    }

    /**
     * This writes a byte array to the [Characteristic.RxServer] characteristic
     */
    private fun write(chunk: ByteArray): Boolean {
        val g = gatt ?: return false
        val service = g.getService(Service.CommandInterface.uuid)
        val characteristic = service.getCharacteristic(Characteristic.RxServer.uuid)
        characteristic.value = chunk
//        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        return g.writeCharacteristic(characteristic)
    }

    /**
     * This enables a notification on the [Characteristic.TxServer] characteristic
     */
    private fun setNotify() {
        val service = gatt?.getService(Service.CommandInterface.uuid)
        val characteristic = service?.getCharacteristic(Characteristic.TxServer.uuid) ?: return
        gatt?.setCharacteristicNotification(characteristic, true)
    }

    // endregion Private Methods
}