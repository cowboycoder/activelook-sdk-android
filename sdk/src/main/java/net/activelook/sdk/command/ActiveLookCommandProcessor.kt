package net.activelook.sdk.command

import android.bluetooth.BluetoothGatt
import androidx.annotation.WorkerThread
import net.activelook.sdk.session.GattSession
import kotlin.math.ceil
import kotlin.math.min

data class CommandResult(val status: Int, val failingFragment: ActiveLookCommandFragment?)

internal data class ActiveLookCommandProcessor(private val gattSession: GattSession) {

    /**
     * This takes an [ActiveLookCommand] and writes its entirety to the [gattSession].
     * The max byte size to write is 20 as specified in the Microoled documentation
     *
     * @param command The [ActiveLookCommand] to enqueue
     */
    @WorkerThread
    fun processCommand(command: ActiveLookCommand): CommandResult {
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
            if(result != BluetoothGatt.GATT_SUCCESS) {
                return CommandResult(result, fragment)
            }
        }
        return CommandResult(BluetoothGatt.GATT_SUCCESS, null)
    }
}