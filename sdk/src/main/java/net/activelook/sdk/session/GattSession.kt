package net.activelook.sdk.session

import android.bluetooth.*
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresPermission
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.command.ActiveLookCommandFragment
import net.activelook.sdk.notification.ActiveLookNotification
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

    sealed class Notification {
        data class BatteryLevel(val percentage: String): Notification()
        data class TxServer(val text: String): Notification()
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
                    this.gatt = gatt
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt.close()
                    cleanup(GattClosedReason.Success)
                }
                BluetoothProfile.STATE_CONNECTING,
                BluetoothProfile.STATE_DISCONNECTING -> {}
            }
        } else {
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
            disconnect()
            return
        }

        val event = Event.Established(this)
        val message = sessionHandler.obtainMessage(0, event)
        sessionHandler.sendMessage(message)

        setNotify(ActiveLookNotification.TxServer)
        setNotify(ActiveLookNotification.BatteryLevel)
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        currentWriteResult = status
        writeLatch?.countDown()
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val value = characteristic.getStringValue(0)
        when(characteristic.uuid) {
            Characteristic.BatteryLevel.uuid -> {
                val batteryLevel = Notification.BatteryLevel(value)
                val message = notificationHandler.obtainMessage(0, batteryLevel)
                notificationHandler.sendMessage(message)
            }
            Characteristic.TxServer.uuid -> {
                val sentText = Notification.TxServer(value)
                val message = notificationHandler.obtainMessage(0, sentText)
                notificationHandler.sendMessage(message)
            }
        }

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
    private fun setNotify(notification: ActiveLookNotification) {
        val service = gatt?.getService(notification.service.uuid)
        val characteristic = service?.getCharacteristic(notification.characteristic.uuid) ?: return
        gatt?.setCharacteristicNotification(characteristic, true)
    }

    // endregion Private Methods
}