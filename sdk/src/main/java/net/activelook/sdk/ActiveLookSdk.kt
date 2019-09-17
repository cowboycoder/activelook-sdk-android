package net.activelook.sdk

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresPermission
import net.activelook.sdk.command.ActiveLookCommand
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.operation.ActiveLookOperationCallback
import net.activelook.sdk.operation.ActiveLookOperationProcessor
import net.activelook.sdk.scanner.BluetoothScanner
import net.activelook.sdk.session.GattClosedReason
import net.activelook.sdk.session.GattSession

class ActiveLookSdk private constructor(private val bleManager: BluetoothManager) {

    // region Static

    companion object {
        internal fun newInstance(context: Context): ActiveLookSdk {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            return ActiveLookSdk(bluetoothManager)
        }

        @Volatile
        private var INSTANCE: ActiveLookSdk? = null

        /**
         * NOTE: It is not the SDK's job to enable Bluetooth
         */
        @JvmStatic
        fun getInstance(context: Context): ActiveLookSdk {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: newInstance(context).also { INSTANCE = it }
            }
        }
    }

    // endregion Static

    // region Scanning

    /**
     * The host application must have ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
     * runtime permission before calling this method, otherwise nothing will happen
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScanning(callback: ScanningCallback) {
        scanner = BluetoothScanner(bleManager.adapter, callback)
        scanner?.startScanning()
    }

    /**
     * The host application must have ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
     * runtime permission before calling this method, otherwise nothing will happen
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
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

    fun connect(unused: Context, device: BluetoothDevice) {

        enqueueDisconnect(device)

        currentSession = GattSession(device, gattSessionHandler, gattNotificationHandler)
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

    fun enqueueOperation(operation: ActiveLookOperation) {
        if (operation is ActiveLookOperation.AddScreen && loadingMode == LoadingMode.LAZY) {
            waitingOperations.add(operation)
            return
        }

        currentSession ?: return

        if (operation is ActiveLookOperation.DisplayScreen && waitingOperations.size > 0) {
            for (waitingOperation in waitingOperations) {
                operationProcessor?.enqueueOperation(waitingOperation)
            }
            waitingOperations.clear()
        }

        operationProcessor?.enqueueOperation(operation)
    }

    // endregion Operations

    // region Private

    private var scanner: BluetoothScanner? = null
    internal var currentSession: GattSession? = null
    private var disconnecting = mutableListOf<GattSession>()
    internal var operationProcessor: ActiveLookOperationProcessor? = null
    var loadingMode: LoadingMode = LoadingMode.NORMAL
    private val waitingOperations: MutableList<ActiveLookOperation> = mutableListOf()

    // endregion Private

    // region GattSessionHandler

    private var gattSessionHandler = Handler {
        when(val sessionEvent = it.obj) {
            is GattSession.Event.Established -> onConnectionEstablished(sessionEvent.session)
            is GattSession.Event.Closed -> onConnectionClosed(sessionEvent.session, sessionEvent.reason)
        }
        true
    }

    private var gattNotificationHandler = Handler {
        val notif = (it.obj as? GattSession.Notification) ?: return@Handler false
        when(notif) {
            is GattSession.Notification.BatteryLevel -> {
                Log.d("TEST", "got BatteryLevel: ${notif.percentage}")
                // TODO: signal to client with notif.percentage
            }
        }
        true
    }

    private fun onConnectionEstablished(gattSession: GattSession) {

        operationProcessor = ActiveLookOperationProcessor(
            gattSession,
            object: ActiveLookOperationCallback {

                override fun activeLookOperationSuccess(operation: ActiveLookOperation) {
                    // TODO: signal to client
                }

                override fun activeLookOperationError(operation: ActiveLookOperation, errorStatus: Int?, failureCommand: ActiveLookCommand) {
                    // TODO: signal to client
                }
            }
        )

        operationProcessor?.enqueueOperation(ActiveLookOperation.Notify.TxServer)
        //operationProcessor?.enqueueOperation(ActiveLookOperation.Notify.BatteryLevel)

        connectionListener?.activeLookConnectionEstablished()
    }

    private fun onConnectionClosed(gattSession: GattSession, closedReason: GattClosedReason) {
        connectionListener?.activeLookConnectionTerminated(closedReason)
        if(gattSession == currentSession) {
            currentSession = null
        }
        disconnecting.remove(gattSession)
    }

    // endregion GattSessionListener
}

enum class LoadingMode {
    NORMAL,
    LAZY
}

internal fun BluetoothDevice.connectGattCompat(unused: Context, autoConnect: Boolean, callback: BluetoothGattCallback) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectGatt(unused, autoConnect, callback, BluetoothDevice.TRANSPORT_LE)
    } else {
        connectGatt(unused, autoConnect, callback)
    }
}