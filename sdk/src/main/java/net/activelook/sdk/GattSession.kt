package net.activelook.sdk

import android.bluetooth.*
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.command.*
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.command.CachePoison
import net.activelook.sdk.command.EnqueueableCommand
import net.activelook.sdk.command.NotificationCommand
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore
import kotlin.math.ceil
import kotlin.math.min

sealed class GattClosedReason(val rawValue: Int) {

    object Success: GattClosedReason(BluetoothGatt.GATT_SUCCESS)
    object DeviceDisconnect: GattClosedReason(0x13)
    object ConnectionTimeout: GattClosedReason(0x08)
    object GattError: GattClosedReason(0x85)
    class Unknown(rawValue: Int): GattClosedReason(rawValue)

    // TODO: add all codes?
    // https://android.googlesource.com/platform/external/bluetooth/bluedroid/+/5738f83aeb59361a0a2eda2460113f6dc9194271/stack/include/gatt_api.h
}

// TODO: need handlers to communicate messages for callbacks
// TODO: ideas -> https://medium.com/@martijn.van.welie/making-android-ble-work-part-3-117d3a8aee23
// TODO: for writes larger than 30 bytes we need to use requestMtu() and onMtuChanged() and adjust accordingly
// TODO: need to find a solution for concatenating commands with `;`

internal class WritableChunk(val data: ByteArray)

internal class GattSession(val device: BluetoothDevice, private val sessionHandler: Handler): BluetoothGattCallback() {

    internal sealed class Event {
        enum class Code { ESTABLISHED, CLOSED }
        data class Established(val session: GattSession): Event()
        data class Closed(val session: GattSession, val reason: GattClosedReason): Event()
    }

    internal enum class MessageCode {
        COMMAND_SUCCESS,
        COMMAND_ERROR
    }

    var commandHandler: Handler? = null

    // region Private Properties

    private var gatt: BluetoothGatt? = null
    private val operationLock = Semaphore(1)
    private val commandLock = Semaphore(1)
    private val writeLock = Semaphore(1)

    private val commandQueue = LinkedBlockingQueue<EnqueueableCommand>()
    private val commandProcessor = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val next = commandQueue.take()
                commandLock.acquire()
                when(next) {
                    is ActiveLookCommand -> enqueueWriteCommand(next)
                    is NotificationCommand -> setNotify()
                    is CachePoison -> commandLock.release()
                }
            }
        } catch(e: InterruptedException) {
            Log.d("TEST", "commandProcessor interrupted", e)
        }
    }

    private val dataQueue = LinkedBlockingQueue<ByteArray>()
    private val dataProcessor = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val next = dataQueue.take()
                writeLock.acquire()
                write(next)
            }
        } catch (e: InterruptedException) {
            Log.d("TEST", "dataProcessor interrupted", e)
        }
    }

    // endregion Private Properties

    init {
        commandProcessor.start()
        dataProcessor.start()
    }

    // region Public Interface

    fun sendCommand(command: EnqueueableCommand) {
        commandQueue.add(command)
    }

    fun disconnect() {
        gatt?.disconnect()
    }

    // endregion Public Interface

    // region BluetoothGattCallback

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if(status == BluetoothGatt.GATT_SUCCESS) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("TEST", "connected")
                    this.gatt = gatt
                    when(device.bondState) {
                        BluetoothDevice.BOND_NONE, BluetoothDevice.BOND_BONDED -> {
                            val delayWhenBonded: Long = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 0 else 1000
                            object: CountDownTimer(delayWhenBonded, 1000) {
                                override fun onFinish() {
                                    gatt.discoverServices()
                                }

                                override fun onTick(millisUntilFinished: Long) {}
                            }
                                .start()
                        }
                        BluetoothDevice.BOND_BONDING -> {
                            Log.d("TEST", "waiting for bonding to complete")
                        }
                    }
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
        val message = sessionHandler.obtainMessage(Event.Code.ESTABLISHED.ordinal, event)
        sessionHandler.sendMessage(message)
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        val value = characteristic.getStringValue(0)
        Log.d("TEST", "got characteristic write status: ${status == BluetoothGatt.GATT_SUCCESS}")
        Log.d("TEST", "got characteristic write notification: $value")
        writeLock.release()
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val value = characteristic.getStringValue(0)
        Log.d("TEST", "got characteristic notification: $value")
        writeLock.release()
    }

    // endregion BluetoothGattCallback

    // region Private Methods

    private fun cleanup(closedReason: GattClosedReason) {
        this.gatt = null
        commandProcessor.interrupt()
        dataProcessor.interrupt()
        val event = Event.Closed(this, closedReason)
        val message = sessionHandler.obtainMessage(Event.Code.CLOSED.ordinal, event)
        sessionHandler.sendMessage(message)
    }

    private fun write(chunk: ByteArray): Boolean {
        val g = gatt ?: return false
        val service = g.getService(Service.CommandInterface.uuid)
        val characteristic = service.getCharacteristic(Characteristic.RxServer.uuid)
        characteristic.value = chunk
//        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        return g.writeCharacteristic(characteristic)
    }

    private fun setNotify() {
        val service = gatt?.getService(Service.CommandInterface.uuid)
        val characteristic = service?.getCharacteristic(Characteristic.TxServer.uuid)
        if(characteristic == null) {
            writeLock.release()
            return
        }
        gatt?.setCharacteristicNotification(characteristic, true)
    }

    private fun enqueueWriteCommand(command: ActiveLookCommand) {
        val chunkSize = 20
        val data = command.data()
        val numOfChunks = ceil(data.size.toDouble() / chunkSize).toInt()
        for(i in 0 until numOfChunks) {
            val start = i * chunkSize
            val length = min(data.size - start, chunkSize)
            val chunk = ByteArray(length)
            System.arraycopy(data, start, chunk, 0, length)
            dataQueue.add(chunk)
        }
    }

    // endregion Private Methods
}