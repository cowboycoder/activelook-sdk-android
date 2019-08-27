package net.activelook.sdk.operation

import android.os.Handler
import android.util.Log
import net.activelook.sdk.session.GattSession
import net.activelook.sdk.session.OperationPoison
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

internal data class ActiveLookOperationProcessor(private val gattSession: GattSession) {

    private val operationLock = Semaphore(1)
    private var operationQueue = LinkedBlockingQueue<ActiveLookOperation>()

    private val executorThread = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val op = operationQueue.take()
                operationLock.acquire()
                for (command in op.commands) {
                    gattSession.sendCommand(command)
                }
                gattSession.sendCommand(OperationPoison(op))
            }
        } catch(e: InterruptedException) {
            Log.d("TEST", "operationQueue dispatcher was interrupted", e)
        }
    }

    private val commandHandler = Handler {
        when(it.obj) {
            is GattSession.CommandResult.Success -> {
                // TODO: Signal result?
            }
            is GattSession.CommandResult.Error -> {
                // TODO: Signal error
            }
        }
        operationLock.release()
        true
    }

    init {
        gattSession.commandResultHandler = commandHandler
        executorThread.start()
    }

    fun enqueueOperation(operation: ActiveLookOperation) {
        operationQueue.add(operation)
    }

    fun shutdown() {
        executorThread.interrupt()
    }
}

