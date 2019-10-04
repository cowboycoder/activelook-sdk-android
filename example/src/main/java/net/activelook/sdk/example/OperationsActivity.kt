package net.activelook.sdk.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_operations.*
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.screen.Screen
import java.io.BufferedReader

class OperationsActivity : AppCompatActivity() {

    private val adapter = OperationListAdapter()

    private val sdkInstance = ActiveLookSdk.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operations)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        operationsList.adapter = adapter

        initOperations()
    }

    private fun initOperations() {
        val jsonStr = assets.open("screen1.json").bufferedReader().use(BufferedReader::readText)
        val screen = Screen.Builder(jsonStr).build()

        val operations = listOf(

            OperationClick(getString(R.string.operation_hello)) {
                sdkInstance.enqueueOperation(ActiveLookOperation.Hello)
            },
            OperationClick(getString(R.string.operation_battery)) {
                sdkInstance.enqueueOperation(ActiveLookOperation.GetBattery)
            },
            OperationSwitch(getString(R.string.operation_display), true) {
                sdkInstance.enqueueOperation(ActiveLookOperation.Display(it))
            },
            OperationClick(getString(R.string.operation_clear)) {
                sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
            },
            OperationSwitch(getString(R.string.operation_led), true) {
                sdkInstance.enqueueOperation(ActiveLookOperation.SetLed(it))
            },
            OperationSliderAndSwitch(
                getString(R.string.operation_brightness),
                true,
                0,
                -50,
                50
            ) { progress, isChecked ->
                sdkInstance.enqueueOperation(
                    ActiveLookOperation.SetBrightness(
                        progress,
                        isChecked
                    )
                )
            },
            OperationClick("Add screen") {
                sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
                sdkInstance.enqueueOperation(ActiveLookOperation.AddScreen(screen))
                sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id, "Dynamic test"))
            },

            OperationClick("Display screen") {
                sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
                sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id, "CHANGED"))
            }
        )

        adapter.submitList(operations)
    }
}