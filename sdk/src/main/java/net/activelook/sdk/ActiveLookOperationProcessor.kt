package net.activelook.sdk

import android.os.Handler
import android.util.Log
import net.activelook.sdk.command.CachePoison
import net.activelook.sdk.operation.ActiveLookOperation
import java.util.concurrent.LinkedBlockingQueue

// TODO: maybe move this back over to the GattSession?
internal data class ActiveLookOperationProcessor(private val gattSession: GattSession) {

    private var operationQueue = LinkedBlockingQueue<ActiveLookOperation>()

    private val executorThread = Thread {
        try {
            while(true) {
                Thread.sleep(30)
                val op = operationQueue.take()
                for (command in op.commands) {
                    gattSession.sendCommand(command)
                }
                gattSession.sendCommand(CachePoison)
            }
        } catch(e: InterruptedException) {
            Log.d("TEST", "operationQueue dispatcher was interrupted", e)
        }
    }

    private val commandHandler = Handler {
//        if(it.what == GattSession.MessageCode.COMMAND_SUCCESS) {
//
//        }
        true
    }

    init {
        gattSession.commandHandler = commandHandler
        executorThread.start()
    }

    fun enqueueOperation(operation: ActiveLookOperation) {
        operationQueue.add(operation)
    }

    fun shutdown() {
        executorThread.interrupt()
    }
}

