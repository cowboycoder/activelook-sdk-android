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

// TODO: ideas -> https://medium.com/@martijn.van.welie/making-android-ble-work-part-3-117d3a8aee23
// TODO: need to find a solution for concatenating commands with `;`?

internal open class GattSession(val device: BluetoothDevice, private val sessionHandler: Handler, notificationHandler: Handler) : BluetoothGattCallback() {

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
    private var overflowLatch: CountDownLatch? = null
    private var currentResult: Int? = null
    private val notificationHandlers: MutableList<Handler> = mutableListOf(notificationHandler)

    // endregion Private Properties

    // region Internal Methods

    fun addNotificationHandler(notificationHandler: Handler) {
        notificationHandlers.add(notificationHandler)
    }

    /**
     * Write an [ActiveLookCommandFragment] to the BLE device
     */
    fun writeCommandFragment(fragment: ActiveLookCommandFragment): Int {
        overflowLatch?.await()
        val l = CountDownLatch(1)
        latch = l
        val success = write(fragment.data)
        if(!success) return -1
        Log.d("TEST", "before await")
        l.await()
        Log.d("TEST", "after await")
        return currentResult ?: -1
    }

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
        Log.d("TEST", "write: $value")
        latch?.countDown()
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        when(characteristic.uuid) {
            Characteristic.BatteryLevel.uuid -> {
                val value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                val batteryLevel = Notification.BatteryLevel(value)

                Log.d("TEST", "battery: $value")

                for (notificationHandler in notificationHandlers) {
                    val message = notificationHandler.obtainMessage(0, batteryLevel)
                    notificationHandler.sendMessage(message)
                }
            }
            Characteristic.TxServer.uuid -> {
                val intValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                val stringValue = characteristic.getStringValue(0)
                val sentText = Notification.TxServer(stringValue.toString())

                Log.d("TEST", "changed: $intValue $stringValue")

                for (notificationHandler in notificationHandlers) {
                    val message = notificationHandler.obtainMessage(0, sentText)
                    notificationHandler.sendMessage(message)
                }
            }
            Characteristic.FlowControl.uuid -> {
                val intValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                val stringValue = characteristic.getStringValue(0)

                Log.d("TEST", "flow: $intValue $stringValue")

                if (intValue == Characteristic.FlowControl.ON) {
                    overflowLatch?.countDown()
                } else if (overflowLatch == null) {
                    overflowLatch = CountDownLatch(1)
                }
            }
        }
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        val value = characteristic.value.map { it.toChar() }.joinToString("")
        Log.d("TEST", "read: $value")
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
        return g.writeCharacteristic(characteristic)
    }

    /**
     * This enables a notification on the [BluetoothGattCharacteristic]
     */
    private fun setNotify(notification: ActiveLookCommand.Notify): Boolean {
        val g = gatt ?: return false
        val service = g.getService(notification.service.uuid)
        val characteristic = service.getCharacteristic(notification.characteristic.uuid)

        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        g.setCharacteristicNotification(characteristic, true)

        val descriptor = characteristic.getDescriptor(Descriptor.ClientCharacteristicConfiguration.uuid)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        return g.writeDescriptor(descriptor)
    }

    // endregion Private Methods
}