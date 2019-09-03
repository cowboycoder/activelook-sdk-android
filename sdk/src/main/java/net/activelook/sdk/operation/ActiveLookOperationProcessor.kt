package net.activelook.sdk.operation

import android.os.Handler
import android.util.Log
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.session.GattSession
import net.activelook.sdk.session.OperationPoison
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

internal interface ActiveLookOperationCallback {
    fun activeLookOperationSuccess(operation: ActiveLookOperation)
    fun activeLookOperationError(operation: ActiveLookOperation, errorStatus: Int?, failureCommand: ActiveLookCommand)
}

internal data class ActiveLookOperationProcessor(private val gattSession: GattSession, private val callback: ActiveLookOperationCallback) {

    private var currentOperation: ActiveLookOperation? = null
    private val operationLock = Semaphore(1)
    private val operationQueue = LinkedBlockingQueue<ActiveLookOperation>()

    private val executorThread = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val op = operationQueue.take()
                operationLock.acquire()
                currentOperation = op
                for (command in op.commands) {
                    gattSession.sendCommand(command)
                }
                gattSession.sendCommand(OperationPoison(op))
            }
        } catch(e: InterruptedException) {
            Log.d("TEST", "operationQueue dispatcher was interrupted", e)
        }
    }

    private val operationResultHandler = Handler { operationResult ->
        when(operationResult.obj) {
            is GattSession.OperationResult.Success -> {
                val op = currentOperation ?: return@Handler true
                callback.activeLookOperationSuccess(op)
            }
            is GattSession.OperationResult.Error -> {
                currentOperation?.let { op ->
                    (operationResult.obj as? GattSession.OperationResult.Error)?.let {
                        callback.activeLookOperationError(op, it.errorStatus, it.failureCommand)
                    }
                }
            }
        }
        currentOperation = null
        operationLock.release()
        true
    }

    init {
        gattSession.operationResultHandler = operationResultHandler
        executorThread.start()
    }

    fun enqueueOperation(operation: ActiveLookOperation) {
        operationQueue.add(operation)
    }

    fun shutdown() {
        executorThread.interrupt()
    }
}

