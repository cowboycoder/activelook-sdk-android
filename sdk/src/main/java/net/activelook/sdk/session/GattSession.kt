package net.activelook.sdk.session

import android.bluetooth.*
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresPermission
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Descriptor
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.command.ActiveLookCommandFragment
import java.util.concurrent.CountDownLatch

// TODO: need handlers to communicate messages for callbacks
// TODO: ideas -> https://medium.com/@martijn.van.welie/making-android-ble-work-part-3-117d3a8aee23
// TODO: need to find a solution for concatenating commands with `;`?

internal open class GattSession(
    val device: BluetoothDevice,
    private val sessionHandler: Handler,
    private val notificationHandler: Handler
) : BluetoothGattCallback() {

    /**
     * Communicates session events to the [sessionHandler]
     */
    sealed class Event {
        data class Established(val session: GattSession): Event()
        data class Closed(val session: GattSession, val reason: GattClosedReason): Event()
    }

    sealed class Notification {
        data class BatteryLevel(val percentage: Int): Notification()
        data class TxServer(val text: String): Notification()
    }

    // region Private Properties

    private var gatt: BluetoothGatt? = null
    private var latch: CountDownLatch? = null
    private var currentResult: Int? = null

    // endregion Private Properties

    // region Internal Methods

    /**
     * Write an [ActiveLookCommandFragment] to the BLE device
     */
//    @Synchronized
    fun writeCommandFragment(fragment: ActiveLookCommandFragment): Int {
        val l = CountDownLatch(1)
        val success = write(fragment.data)
        if(!success) return -1
        latch = l
        l.await()
        return currentResult ?: -1
    }

//    @Synchronized
    fun notifyCommand(command: ActiveLookCommand.Notify): Int {
        val l = CountDownLatch(1)
        val success = setNotify(command)
        if(!success) return -1
        latch = l
        l.await()
        return currentResult ?: -1
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

        for(serv in gatt.services) {
            if(serv.uuid == Service.Battery.uuid) {
                for (ch in serv.characteristics) {
                    if(ch.uuid == Characteristic.BatteryLevel.uuid) {
                        for(desc in ch.descriptors) {
                            Log.e("TEST", "found battery descriptor: ${desc.uuid}")
                        }
                    }
                }
            }
        }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        currentResult = status
        val value = characteristic.getStringValue(0)
        Log.d("TEST", "got characteristic write notification: $value")
        latch?.countDown()
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        when(characteristic.uuid) {
            Characteristic.BatteryLevel.uuid -> {
                val value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                val batteryLevel = Notification.BatteryLevel(value)
                val message = notificationHandler.obtainMessage(0, batteryLevel)
                notificationHandler.sendMessage(message)
            }
            Characteristic.TxServer.uuid -> {
                val value = characteristic.getStringValue(0)
                val sentText = Notification.TxServer(value)
                val message = notificationHandler.obtainMessage(0, sentText)
                notificationHandler.sendMessage(message)
            }
        }
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        currentResult = status
        latch?.countDown()
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
    private fun setNotify(notification: ActiveLookCommand.Notify): Boolean {
        val g = gatt ?: return false
        val service = g.getService(notification.service.uuid)
        val characteristic = service.getCharacteristic(notification.characteristic.uuid)
        val descriptor = characteristic.getDescriptor(Descriptor.ClientCharacteristicConfiguration.uuid)
        g.setCharacteristicNotification(characteristic, true)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        return g.writeDescriptor(descriptor)
    }

    // endregion Private Methods
}