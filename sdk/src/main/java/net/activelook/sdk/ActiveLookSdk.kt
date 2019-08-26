package net.activelook.sdk

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import androidx.annotation.RequiresPermission
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.scanner.BluetoothScanner

class ActiveLookSdk(private val bleManager: BluetoothManager) {

    // region Static

    companion object {

        lateinit var shared: ActiveLookSdk

        /**
         * NOTE: It is not the SDK's job to enable Bluetooth
         */
        fun setup(bleManager: BluetoothManager) {
            shared = ActiveLookSdk(bleManager)
        }
    }

    // endregion Static

    // region Scanning

    /**
     * The host application must have ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
     * runtime permission before calling this method, otherwise nothing will happen
     */
    @RequiresPermission(
        allOf = arrayOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    fun startScanning(callback: ScanningCallback) {
        scanner = BluetoothScanner(bleManager.adapter, callback)
        scanner?.startScanning()
    }

    fun stopScanning() {
        scanner?.stopScanning()
        scanner = null
    }

    interface ScanningCallback {

        fun foundDevices(devices: List<BluetoothDevice>)
        fun scanError(err: Error.ScanFailed)

        sealed class Error {

            data class ScanFailed(val reason: Code): Error() {

                enum class Code {
                    SCAN_FAILED_ALREADY_STARTED,
                    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED,
                    SCAN_FAILED_FEATURE_UNSUPPORTED,
                    SCAN_FAILED_INTERNAL_ERROR,
                    SCAN_FAILED_UNKNOWN
                }
            }
        }
    }

    // endregion Scanning

    // region Connection

    interface ConnectionListener {
        fun activeLookConnectionEstablished()
        fun activeLookConnectionTerminated(reason: GattClosedReason)
    }

    var connectionListener: ConnectionListener? = null

    // TODO: we might need to add a manual connection timer to track timeouts if connection hangs...

    fun connect(unused: Context, device: BluetoothDevice) {

        enqueueDisconnect(device)

        currentSession = GattSession(device, gattSessionHandler)
        device.connectGattCompat(unused, false, currentSession!!)
    }

    fun disconnect(device: BluetoothDevice) {
        enqueueDisconnect(device)
    }

    private fun enqueueDisconnect(device: BluetoothDevice) {
        val session = if(currentSession?.device == device) currentSession!! else return // unknown device or already disconnecting
        disconnecting.add(session)
        session.disconnect()
        currentSession = null
        operationProcessor?.shutdown()
    }

    // endregion Connection

    // region Operations

    fun sayHello() {
        currentSession ?: return
        operationProcessor?.enqueueOperation(ActiveLookOperation.Hello)
    }

    fun getBattery() {
        currentSession ?: return
        operationProcessor?.enqueueOperation(ActiveLookOperation.GetBattery)
    }

    // endregion Operations

    // region Private

    private var scanner: BluetoothScanner? = null
    private var currentSession: GattSession? = null
    private var disconnecting = mutableListOf<GattSession>()
    private var operationProcessor: ActiveLookOperationProcessor? = null

    // endregion Private

    // region GattSessionHandler

    private var gattSessionHandler = Handler {
        val event = it.getGattSessionEvent() ?: return@Handler false
        when(event) {
            is GattSession.Event.Established -> {
                operationProcessor = ActiveLookOperationProcessor(event.session)
                connectionListener?.activeLookConnectionEstablished()
            }
            is GattSession.Event.Closed -> {
                connectionListener?.activeLookConnectionTerminated(event.reason)
                if(event.session == currentSession) {
                    currentSession == null
                }
                disconnecting.remove(event.session)
            }
        }
        true
    }

    // endregion GattSessionListener
}

internal fun BluetoothDevice.connectGattCompat(unused: Context, autoConnect: Boolean, callback: BluetoothGattCallback) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectGatt(unused, autoConnect, callback, BluetoothDevice.TRANSPORT_LE)
    } else {
        connectGatt(unused, autoConnect, callback)
    }
}

internal fun Message.getGattSessionEvent(): GattSession.Event? {
    try {
        when(what) {
            GattSession.Event.Code.CLOSED.ordinal -> {
                return obj as GattSession.Event.Closed
            }
            GattSession.Event.Code.ESTABLISHED.ordinal -> {
                return obj as GattSession.Event.Established
            }
        }
    } catch(t: Throwable) {}
    return null
}