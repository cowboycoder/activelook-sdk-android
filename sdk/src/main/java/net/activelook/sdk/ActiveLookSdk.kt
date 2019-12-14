package net.activelook.sdk

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
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
import net.activelook.sdk.util.connectGattCompat
import java.util.concurrent.Semaphore

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
         * Get an instance of the SDK.
         *
         * **Note: It is not the SDK's job to enable Bluetooth.**
         *
         * @param context Context to initialize the SDK
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
     * Start scanning devices. The devices fount will be returned in the [ScanningCallback] you provide.
     *
     * **Note: The host application must have [Manifest.permission.ACCESS_COARSE_LOCATION] or [Manifest.permission.ACCESS_FINE_LOCATION]
     * runtime permission before calling this method, otherwise nothing will happen.**
     * @param callback Callback used to deliver scan results.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun startScanning(callback: ScanningCallback) {
        scanner = BluetoothScanner(bleManager.adapter, callback)
        scanner?.startScanning()
    }

    /**
     * Stop scanning.
     *
     * **Note: The host application must have [Manifest.permission.ACCESS_COARSE_LOCATION] or [Manifest.permission.ACCESS_FINE_LOCATION]
     * runtime permission before calling this method, otherwise nothing will happen.**
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun stopScanning() {
        scanner?.stopScanning()
        scanner = null
    }

    /**
     * Callback for scan event.
     */
    interface ScanningCallback {

        /**
         * Call when a new BLE device is found.
         *
         * @param devices List of every BLE devices found, some devices may be out of range
         */
        fun foundDevices(devices: List<BluetoothDevice>)

        /**
         * Call when an error appears during the scan
         *
         * @param err The reason of the failure
         */
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

    /**
     * Listen to connection event
     */
    interface ConnectionListener {
        /**
         * This method will be triggered each time a BLE device is connected.
         */
        fun activeLookConnectionEstablished()

        /**
         * This method will be triggered each time a BLE device is disconnected.
         *
         * @param reason Reason of the disconnection
         */
        fun activeLookConnectionTerminated(reason: GattClosedReason)
    }

    var connectionListener: ConnectionListener? = null

    /**
     * Connect a BLE device
     *
     * When the device will be connected, the method
     * [ConnectionListener.activeLookConnectionEstablished] will be triggered.
     *
     * @param unused Used only to connect the device
     * @param device The device to connect
     */
    fun connect(unused: Context, device: BluetoothDevice) {

        enqueueDisconnect(device)

        currentSession = GattSession(device, gattSessionHandler, gattNotificationHandler)
        device.connectGattCompat(unused, false, currentSession!!)
    }

    /**
     * Disconnect a BLE device.
     *
     * When the device will be disconnect, the method
     * [ConnectionListener.activeLookConnectionTerminated] will be triggered with the reason of the
     * disconnection.
     *
     * @param device The device to disconnect
     */
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

    /**
     * Add an operation to the queue.
     *
     * If the loading mode is set to [LoadingMode.NORMAL], the operation will be executed
     * when possible. If the loading mode is set to [LoadingMode.LAZY] and
     * the operation is [ActiveLookOperation.AddScreen],
     * it will be postponed until a [ActiveLookOperation.ShowScreen] is enqueue.
     *
     *
     * @param operation [ActiveLookOperation] to enqueue
     */
    fun enqueueOperation(operation: ActiveLookOperation) {
        if (operation is ActiveLookOperation.AddScreen && loadingMode == LoadingMode.LAZY) {
            waitingOperations.add(operation)
            return
        }

        currentSession ?: return

        if (operation is ActiveLookOperation.ShowScreen && waitingOperations.size > 0) {
            for (waitingOperation in waitingOperations) {
                operationProcessor?.enqueueOperation(waitingOperation)
            }
            waitingOperations.clear()
        }

        operationProcessor?.enqueueOperation(operation)
    }

    // endregion Operations

    // region Public

    var loadingMode: LoadingMode = LoadingMode.NORMAL

    // endregion Public

    // region Private

    private var scanner: BluetoothScanner? = null
    internal var currentSession: GattSession? = null
    private var disconnecting = mutableListOf<GattSession>()
    internal var operationProcessor: ActiveLookOperationProcessor? = null
    private val waitingOperations: MutableList<ActiveLookOperation> = mutableListOf()
    private var lastBitmapId = -1
    private var lastLayoutId = -1
    private val notificationMutex = Semaphore(1)

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

                override fun activeLookNotification(code: Int, message: String) {
                    notificationMutex.acquire()
                    Log.d("TEST", "code: $code, message: $message")
                    if (code == 3) {
                        val regexLayout = """#(\d+) (\d+) x (\d+)""".toRegex()
                        val result = regexLayout.find(message)
                        val layoutId = result?.groupValues?.get(1)?.toIntOrNull() ?: return
                        if (layoutId > lastBitmapId) {
                            lastBitmapId = layoutId
                        }
                        val width = result?.groupValues?.get(2)?.toIntOrNull() ?: return
                        val height = result?.groupValues?.get(3)?.toIntOrNull() ?: return
                        addStoredBitmap(layoutId, width, height)
                    }
                    notificationMutex.release()
                }
            }
        )

        operationProcessor?.enqueueOperation(ActiveLookOperation.Notify.TxServer)
        operationProcessor?.enqueueOperation(ActiveLookOperation.Notify.BatteryLevel)
        operationProcessor?.enqueueOperation(ActiveLookOperation.Notify.Flow)
        operationProcessor?.enqueueOperation(ActiveLookOperation.ListBitmaps)

        connectionListener?.activeLookConnectionEstablished()
    }

    private fun onConnectionClosed(gattSession: GattSession, closedReason: GattClosedReason) {
        connectionListener?.activeLookConnectionTerminated(closedReason)
        if(gattSession == currentSession) {
            currentSession = null
        }
        disconnecting.remove(gattSession)
        lastBitmapId = -1
    }

    // endregion GattSessionListener

    // region BitmapManager

    class StoredBitmap {
        var index: Int = 0

        var id: Int = -1

        var width: Int = 0
        var height: Int = 0

        constructor(i: Int, w: Int, h: Int) {
            index = i
            width = w
            height = h
        }
    }

    internal val storedBitmaps : MutableList<StoredBitmap> = mutableListOf()

    private fun addStoredBitmap(index: Int, w: Int, h: Int) {
        storedBitmaps += StoredBitmap(index, w, h)
    }
}
