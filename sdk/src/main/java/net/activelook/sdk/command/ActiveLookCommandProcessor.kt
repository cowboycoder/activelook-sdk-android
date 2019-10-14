package net.activelook.sdk.command

import android.bluetooth.BluetoothGatt
import android.util.Log
import androidx.annotation.WorkerThread
import net.activelook.sdk.session.GattSession
import kotlin.math.ceil
import kotlin.math.min

internal data class CommandResult(val status: Int, val failingFragment: ActiveLookCommandFragment?)

internal data class ActiveLookCommandProcessor(private val gattSession: GattSession) {

    /**
     * This takes an [ActiveLookCommand] and writes its entirety to the [gattSession].
     * The max byte size to write is 20 as specified in the Microoled documentation
     *
     * @param command The [ActiveLookCommand] to enqueue
     */
    @WorkerThread
    fun processCommand(command: ActiveLookCommand): CommandResult {
        return when(command) {
            is ActiveLookCommand.Notify -> notifyCommand(command)
            else -> writeCommand(command)
        }
    }

    private fun notifyCommand(command: ActiveLookCommand.Notify): CommandResult {
        val result = gattSession.notifyCommand(command)
        return CommandResult(result, null)
    }

    private fun writeCommand(command: ActiveLookCommand): CommandResult {
        val chunkSize = 20
        val data = command.data()
        val numOfChunks = ceil(data.size.toDouble() / chunkSize).toInt()

        for(i in 0 until numOfChunks) {
            val start = i * chunkSize
            val length = min(data.size - start, chunkSize)
            val chunk = ByteArray(length)
            System.arraycopy(data, start, chunk, 0, length)
            val fragment = ActiveLookCommandFragment(chunk)
            val result = gattSession.writeCommandFragment(fragment)
            Log.d(
                "TEST",
                "writing (${(i.toDouble()) / numOfChunks * 100}%):\t${String(chunk)}\tresult: $result"
            )
//            Thread.sleep(1000)
            if(result != BluetoothGatt.GATT_SUCCESS) {
                return CommandResult(result, fragment)
            }
        }
        Log.d("TEST", "write finished")
        return CommandResult(BluetoothGatt.GATT_SUCCESS, null)
    }
}