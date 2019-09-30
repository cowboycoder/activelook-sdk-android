package net.activelook.sdk.example

import android.graphics.Color
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_operations.*
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.screen.Orientation
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.util.Point
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileReader

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
        val screen = Screen.Builder(jsonStr)

//        val screen = Screen.Builder(1)
//            .setPadding(0, 25, 0, 0)
//            .setText(Point(200, 200), Orientation.R4, true)
//            .setForegroundColor(Color.WHITE)
//            .addWidget(TextWidget(50, 50, "Hello!", null, Color("#FFFFFF")))
//            .addWidget(CircleWidget(150, 100, 20, true, Color("#FFFFFF")))
//            .addWidget(LineWidget(0, 100, 100, 150, Color("#888888")))
//            .addWidget(PointWidget(100, 100, Color("#15FF13")))
//            .addWidget(RectangleWidget(200, 50, 20, 30, true, Color("#777777")))
//            .addWidget(
//                BitmapWidget(
//                    30,
//                    30,
//                    200,
//                    200,
//                    listOf(BitmapWidget.BitmapSource("refresh", "assets/refresh/png")),
//                    "refresh",
//                    15
//                )
//            )
//            .addWidget(
//                BitmapWidget(
//                    20, 20, 100, 100, listOf(
//                        BitmapWidget.BitmapSource("refresh", "assets/refresh/png"),
//                        BitmapWidget.BitmapSource("refresh", "assets/refresh/png")
//                    ), "refresh", 15
//                )
//            )
//            .addWidget(
//                BitmapWidget(
//                    10,
//                    10,
//                    100,
//                    100,
//                    listOf(BitmapWidget.BitmapSource("refresh", "assets/refresh/png")),
//                    "refresh",
//                    15
//                )
//            )
            .build()

        val operations = listOf(

            OperationClick(getString(R.string.operation_hello)) {
                sdkInstance.enqueueOperation(ActiveLookOperation.Hello)
            },

            OperationClick("Version") {
                sdkInstance.enqueueOperation(ActiveLookOperation.Version)
            },

            OperationClick(getString(R.string.operation_battery)) {
                sdkInstance.enqueueOperation(ActiveLookOperation.GetBattery)
            },

            OperationSwitch(getString(R.string.operation_display), true) {
                sdkInstance.enqueueOperation(ActiveLookOperation.Display(it))
            },

            OperationSwitch("Debug", false) {
                sdkInstance.enqueueOperation(ActiveLookOperation.SetDebug(it))
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

            OperationClick("Delete screen") {
                sdkInstance.enqueueOperation(ActiveLookOperation.DeleteScreen(screen.id))
            },

            OperationClick("Add screen") {
                sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
                sdkInstance.enqueueOperation(ActiveLookOperation.AddScreen(screen, contentResolver))
                sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id, "Dynamic test"))
            },

            OperationClick("Display screen") {
                sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
                sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id, "CHANGED"))
            },

            OperationClick("Add bitmap") {
                val bitmap = ResourcesCompat.getDrawable(resources, R.drawable.ic_refresh_white_24dp, null)?.toBitmap()
                if (bitmap != null) {
                    sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
                    sdkInstance.enqueueOperation(ActiveLookOperation.AddBitmap(bitmap))
                }
            }
        )

        adapter.submitList(operations)
    }
}