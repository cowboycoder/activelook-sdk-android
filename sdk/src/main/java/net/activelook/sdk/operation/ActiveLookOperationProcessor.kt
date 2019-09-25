package net.activelook.sdk.operation

import android.bluetooth.BluetoothGatt
import android.os.Handler
import android.util.Log
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.command.ActiveLookCommandProcessor
import net.activelook.sdk.command.NeedPreviousResult
import net.activelook.sdk.command.NeedPreviousResults
import net.activelook.sdk.session.GattSession
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue

internal interface ActiveLookOperationCallback {
    fun activeLookOperationSuccess(operation: ActiveLookOperation)
    fun activeLookNotification(code: Int, message: String)
    fun activeLookOperationError(operation: ActiveLookOperation, errorStatus: Int?, failureCommand: ActiveLookCommand)
}

internal open class ActiveLookOperationProcessor(
    gattSession: GattSession,
    private val callback: ActiveLookOperationCallback
) {

    private val operationQueue = LinkedBlockingQueue<ActiveLookOperation>()
    private val commandProcessor = ActiveLookCommandProcessor(gattSession)
    private var lastResults = mutableListOf<String>()
    private var latch: CountDownLatch? = null
    private val gattNotificationHandler = Handler {
        val notif = (it.obj as? GattSession.Notification) ?: return@Handler false
        when (notif) {
            is GattSession.Notification.TxServer -> {
                lastResults.add(notif.text)
                latch?.countDown()
                Log.d("TEST", "got result: ${notif.text}")
                callback.activeLookNotification(notif.text[0].toInt(), notif.text.substring(1))
            }
        }
        true
    }

    private val executorThread = Thread {
        try {
            while(true) {
                val op = operationQueue.take()
                lastResults.clear()
                for (command in op.commands) {
                    Log.d("TEST", "operationQueue command $command")
                    if (command is NeedPreviousResult) {
                        latch = CountDownLatch(1)
                        latch?.await()
                        command.setPreviousResult(lastResults.last())
                    }

                    if (command is NeedPreviousResults) {
                        latch = CountDownLatch(1)
                        latch?.await()
                        command.setPreviousResults(lastResults)
                    }
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
        gattSession.addNotificationHandler(gattNotificationHandler)
        executorThread.start()
    }

    fun enqueueOperation(operation: ActiveLookOperation) {
        operationQueue.add(operation)
    }

    fun shutdown() {
        executorThread.interrupt()
    }
}

