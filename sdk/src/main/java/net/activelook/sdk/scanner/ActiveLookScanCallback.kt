package net.activelook.sdk.scanner

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.blemodel.Device
import java.lang.ref.WeakReference

/**
 * This is an wrapper for the API 21 [ScanCallback] that delivers results to the
 * SDK's [ActiveLookSdk.ScanningCallback]
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class ActiveLookScanCallback(callback: ActiveLookSdk.ScanningCallback): ScanCallback() {

    private val callbackRef = WeakReference(callback)

    override fun onScanFailed(errorCode: Int) {

        val callback = callbackRef.get() ?: return

        val reason: ActiveLookSdk.ScanningCallback.Error.ScanFailed.Code = when(errorCode) {
            SCAN_FAILED_ALREADY_STARTED -> ActiveLookSdk.ScanningCallback.Error.ScanFailed.Code.SCAN_FAILED_ALREADY_STARTED
            SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> ActiveLookSdk.ScanningCallback.Error.ScanFailed.Code.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED
            SCAN_FAILED_FEATURE_UNSUPPORTED -> ActiveLookSdk.ScanningCallback.Error.ScanFailed.Code.SCAN_FAILED_FEATURE_UNSUPPORTED
            SCAN_FAILED_INTERNAL_ERROR -> ActiveLookSdk.ScanningCallback.Error.ScanFailed.Code.SCAN_FAILED_INTERNAL_ERROR
            else -> ActiveLookSdk.ScanningCallback.Error.ScanFailed.Code.SCAN_FAILED_UNKNOWN
        }

        callback.scanError(ActiveLookSdk.ScanningCallback.Error.ScanFailed(reason))
    }

    @RequiresPermission("android.permission.BLUETOOTH")
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        result?.let {
            onBatchScanResults(mutableListOf(it))
        }
    }

    @RequiresPermission("android.permission.BLUETOOTH")
    override fun onBatchScanResults(rawResults: MutableList<ScanResult>?) {
        rawResults?.let { results ->
            val callback = callbackRef.get() ?: return
            results
                .filter {
                    for(recog in Device.RECOGNIZED_DEVICE_NAMES) {
                        if(it.device.name?.toLowerCase()?.contains(recog) == true) {
                            return@filter true
                        }
                    }
                    false
                }
                .map { it.device }
                .let { callback.foundDevices(it) }
        }
    }
}