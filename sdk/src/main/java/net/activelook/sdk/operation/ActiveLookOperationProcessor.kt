package net.activelook.sdk.operation

import android.bluetooth.BluetoothGatt
import android.util.Log
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.command.ActiveLookCommandProcessor
import net.activelook.sdk.session.GattSession
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

internal interface ActiveLookOperationCallback {
    fun activeLookOperationSuccess(operation: ActiveLookOperation)
    fun activeLookOperationError(operation: ActiveLookOperation, errorStatus: Int?, failureCommand: ActiveLookCommand)
}

internal data class ActiveLookOperationProcessor(private val gattSession: GattSession, private val callback: ActiveLookOperationCallback) {

    private val operationQueue = LinkedBlockingQueue<ActiveLookOperation>()
    private val commandProcessor = ActiveLookCommandProcessor(gattSession)

    private val executorThread = Thread {
        try {
            while(true) {
                val op = operationQueue.take()
                for (command in op.commands) {
                    val result = commandProcessor.processCommand(command)
                    when(result.status) {
                        BluetoothGatt.GATT_SUCCESS -> callback.activeLookOperationSuccess(op)
                        else -> callback.activeLookOperationError(op, result.status, command)
                    }
                }
            }
        } catch(e: InterruptedException) {
            Log.d("TEST", "operationQueue dispatcher was interrupted", e)
        }
    }

    init {
        executorThread.start()
    }

    fun enqueueOperation(operation: ActiveLookOperation) {
        operationQueue.add(operation)
    }

    fun shutdown() {
        executorThread.interrupt()
    }
}

