package net.activelook.sdk.session

import android.bluetooth.*
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

// TODO: need handlers to communicate messages for callbacks
// TODO: ideas -> https://medium.com/@martijn.van.welie/making-android-ble-work-part-3-117d3a8aee23
// TODO: need to find a solution for concatenating commands with `;`?

internal class GattSession(val device: BluetoothDevice, private val sessionHandler: Handler): BluetoothGattCallback() {

    /**
     * A [ByteArray] encapsulation that makes it [Enqueueable]
     */
    class EnqueueableByteArray(val data: ByteArray): Enqueueable

    /**
     * Communicates session events to the [sessionHandler]
     */
    sealed class Event {
        data class Established(val session: GattSession): Event()
        data class Closed(val session: GattSession, val reason: GattClosedReason): Event()
    }

    /**
     * Communicates [ActiveLookCommand] results to the [operationResultHandler]
     */
    sealed class OperationResult {
        companion object {
            @JvmStatic
            fun construct(command: ActiveLookCommand?, status: Int?): OperationResult? {
                val c = command ?: return null
                val s = status ?: return null
                return when(s) {
                    BluetoothGatt.GATT_SUCCESS -> Success
                    else -> Error(c, s)
                }
            }
        }

        object Success: OperationResult()
        data class Error(val failureCommand: ActiveLookCommand, val errorStatus: Int): OperationResult()
    }

    // region Internal Properties

    /**
     * used to communicate [OperationResult] events on another thread
     */
    var operationResultHandler: Handler? = null

    // endregion Internal Properties

    // region Private Properties

    private var gatt: BluetoothGatt? = null
    private var currentCommand: ActiveLookCommand? = null
    private var currentResult: Int? = null

    // Command Processing
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
                        val result = OperationResult.construct(currentCommand, currentResult)
                        if(result != null) {
                            val resultMessage = operationResultHandler?.obtainMessage(0, result)
                            operationResultHandler?.sendMessage(resultMessage)
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

    // Write Processing
    private val writeLock = Semaphore(1)
    private val writeQueue = LinkedBlockingQueue<Enqueueable>()
    private val writeProcessor = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val next = writeQueue.take()
                writeLock.acquire()
                when(next) {
                    is EnqueueableByteArray -> {

                        // If we receive an error status for one of our command fragments, we ignore the rest
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
            Log.d("TEST", "writeProcessor interrupted", e)
        }
    }

    // endregion Private Properties

    init {
        commandProcessor.start()
        writeProcessor.start()
    }

    // region Internal Methods

    /**
     * Send an [Enqueueable] command to the bluetooth device
     */
    fun sendCommand(command: Enqueueable) {
        commandQueue.add(command)
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
        commandLock.release()
    }

    // endregion BluetoothGattCallback

    // region Private Methods

    /**
     * When the GattSession is closed we stop our processor threads and send
     * a message to the sessionHandler
     */
    private fun cleanup(closedReason: GattClosedReason) {
        this.gatt = null
        commandProcessor.interrupt()
        writeProcessor.interrupt()
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
        val characteristic = service?.getCharacteristic(Characteristic.TxServer.uuid)
        if(characteristic == null) {
            commandLock.release()
            return
        }
        gatt?.setCharacteristicNotification(characteristic, true)
    }

    /**
     * This takes an [ActiveLookCommand] and enqueues its entirety in the [writeQueue].
     * The max byte size to write is 20 as specified in the Microoled documentation
     *
     * @param command The [ActiveLookCommand] to enqueue
     */
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
            writeQueue.add(EnqueueableByteArray(chunk))
        }
        writeQueue.add(CommandPoison(command))
    }

    // endregion Private Methods
}