package net.activelook.sdk.session

import android.bluetooth.*
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresPermission
import net.activelook.sdk.blemodel.Characteristic
import net.activelook.sdk.blemodel.Service
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.command.Enqueueable
import net.activelook.sdk.command.NotificationCommand
import net.activelook.sdk.command.data
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
// TODO: need to find a solution for concatenating commands with `;`?

internal class EnqueuableByteArray(val data: ByteArray): Enqueueable

internal class GattSession(val device: BluetoothDevice, private val sessionHandler: Handler): BluetoothGattCallback() {

    internal sealed class Event {
        enum class Code { ESTABLISHED, CLOSED }
        data class Established(val session: GattSession): Event()
        data class Closed(val session: GattSession, val reason: GattClosedReason): Event()
    }

    internal sealed class CommandResult {
        companion object {
            @JvmStatic
            fun construct(command: ActiveLookCommand?, status: Int?): CommandResult? {
                val c = command ?: return null
                val s = status ?: return null
                return when(s) {
                    BluetoothGatt.GATT_SUCCESS -> Success(c)
                    else -> Error(c, s)
                }
            }
        }
        data class Success(val command: ActiveLookCommand): CommandResult()
        data class Error(val command: ActiveLookCommand, val errorStatus: Int): CommandResult()
    }

    internal var commandResultHandler: Handler? = null

    // region Private Properties

    private var gatt: BluetoothGatt? = null
    private var currentCommand: ActiveLookCommand? = null
    private var currentResult: Int? = null

    private val commandLock = Semaphore(1)
    private val commandQueue = LinkedBlockingQueue<Enqueueable>()
    private val commandProcessor = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val next = commandQueue.take()
                commandLock.acquire()
                when(next) {
                    is ActiveLookCommand -> enqueueWriteCommand(next)
                    is NotificationCommand -> setNotify()
                    is OperationPoison -> {
                        val result = CommandResult.construct(currentCommand, currentResult)
                        if(result != null) {
                            val resultMessage = commandResultHandler?.obtainMessage(0, result)
                            commandResultHandler?.sendMessage(resultMessage)
                        }
                        currentCommand = null
                        currentResult = null
                        commandLock.release()
                    }
                }
            }
        } catch(e: InterruptedException) {
            Log.d("TEST", "commandProcessor interrupted", e)
        }
    }

    private val writeLock = Semaphore(1)
    private val dataQueue = LinkedBlockingQueue<Enqueueable>()
    private val dataProcessor = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val next = dataQueue.take()
                writeLock.acquire()
                when(next) {
                    is EnqueuableByteArray -> {
                        if(currentResult != null && currentResult != BluetoothGatt.GATT_SUCCESS) {
                            writeLock.release()
                        } else {
                            write(next.data)
                        }
                    }
                    is CommandPoison -> {
                        writeLock.release()
                        commandLock.release()
                    }
                }
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

    fun sendCommand(command: Enqueueable) {
        commandQueue.add(command)
    }

    fun disconnect() {
        gatt?.disconnect()
    }

    // endregion Public Interface

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
        val message = sessionHandler.obtainMessage(Event.Code.ESTABLISHED.ordinal, event)
        sessionHandler.sendMessage(message)
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {

        currentResult = status
        Log.d("TEST", "got characteristic write status: ${status == BluetoothGatt.GATT_SUCCESS}")

        // TODO: remove after debugging
        when(status) {
            BluetoothGatt.GATT_SUCCESS -> {
                try {
                    val value = characteristic.getStringValue(0)
                    Log.d("TEST", "got characteristic write notification: $value")
                } catch(t: Throwable) {}
            }
        }

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
        currentCommand = command
        val chunkSize = 20
        val data = command.data()
        val numOfChunks = ceil(data.size.toDouble() / chunkSize).toInt()
        for(i in 0 until numOfChunks) {
            val start = i * chunkSize
            val length = min(data.size - start, chunkSize)
            val chunk = ByteArray(length)
            System.arraycopy(data, start, chunk, 0, length)
            dataQueue.add(EnqueuableByteArray(chunk))
        }
        dataQueue.add(CommandPoison(command))
    }

    // endregion Private Methods
}