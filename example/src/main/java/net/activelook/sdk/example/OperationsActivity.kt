package net.activelook.sdk.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_operations.*
import kotlinx.android.synthetic.main.list_item_operation.view.*
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.screen.TextWidget

class OperationsActivity : AppCompatActivity() {

    private val adapter = OperationListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operations)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        operationsList.adapter = adapter

        initOperations()
    }

    private fun initOperations() {
        val screen = Screen.Builder(10)
            .setPadding(0, 0, 0, 0)
            .addWidget(TextWidget(150, 150, "Hello!"))
            .build()


        val operations = listOf(
            OperationClick(getString(R.string.operation_hello)) {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.Hello)
            },
            OperationClick(getString(R.string.operation_battery)) {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.GetBattery)
            },
            OperationSwitch(getString(R.string.operation_display), true) {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.Display(it))
            },
            OperationClick(getString(R.string.operation_clear)) {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.ClearScreen)
            },
            OperationSwitch(getString(R.string.operation_led), true) {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.SetLed(it))
            },
            OperationSliderAndSwitch(
                getString(R.string.operation_brightness),
                true,
                0,
                -50,
                50
            ) { progress, isChecked ->
                ActiveLookSdk.shared.enqueueOperation(
                    ActiveLookOperation.SetBrightness(
                        progress,
                        isChecked
                    )
                )
            },
            OperationClick("Save screen") {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.AddScreen(screen))
            },
            OperationClick("Delete screen") {
                ActiveLookSdk.shared.enqueueOperation(ActiveLookOperation.DeleteScreen(screen.id))
            },
            OperationClick("Display screen") {
                ActiveLookSdk.shared.enqueueOperation(
                    ActiveLookOperation.DisplayScreen(
                        screen.id,
                        "Hi"
                    )
                )
            }
        )

        adapter.submitList(operations)
    }
}

interface Operation {
    val name: String
}

class OperationClick(override val name: String, val onPlay: () -> Unit) : Operation

class OperationSwitch(
    override val name: String,
    val defaultValue: Boolean,
    val onPlay: (isChecked: Boolean) -> Unit
) : Operation

class OperationSliderAndSwitch(
    override val name: String,
    val defaultIsChecked: Boolean,
    val defaultProgress: Int,
    val min: Int,
    val max: Int,
    val onPlay: (progress: Int, isChecked: Boolean) -> Unit
) : Operation

class OperationListAdapter :
    ListAdapter<Operation, OperationListAdapter.OperationViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item_operation, parent, false)

        return OperationViewHolder(view)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        val operation = getItem(position)
        holder.bind(operation)
    }

    class OperationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(operation: Operation) {
            itemView.operationLabel.text = operation.name

            when (operation) {
                is OperationClick -> {
                    hideSwitch()
                    hideSlider()

                    itemView.operationPlay.setOnClickListener { operation.onPlay() }
                }
                is OperationSwitch -> {
                    showSwitch()
                    hideSlider()

                    itemView.operationSwitch.isChecked = operation.defaultValue

                    itemView.operationPlay.setOnClickListener {
                        val isChecked = itemView.operationSwitch.isChecked
                        operation.onPlay(isChecked)
                    }
                }
                is OperationSliderAndSwitch -> {
                    showSwitch()
                    showSlider()

                    itemView.operationSwitch.isChecked = operation.defaultIsChecked

                    itemView.operationSlider.max = operation.max - operation.min
                    itemView.operationSlider.progress = operation.defaultProgress - operation.min
                    setSliderValue(itemView.operationSlider.progress, operation.min)

                    itemView.operationSlider.setOnSeekBarChangeListener(
                        object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                setSliderValue(progress, operation.min)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                        }
                    )

                    itemView.operationPlay.setOnClickListener {
                        val progress = itemView.operationSlider.progress + operation.min
                        val isChecked = itemView.operationSwitch.isChecked
                        operation.onPlay(progress, isChecked)
                    }
                }
            }
        }

        private fun hideSwitch() {
            itemView.operationSwitch.visibility = View.GONE
        }

        private fun showSwitch() {
            itemView.operationSwitch.visibility = View.VISIBLE
        }

        private fun hideSlider() {
            itemView.operationSlider.visibility = View.GONE
            itemView.operationSliderValue.visibility = View.GONE
            itemView.operationSlider.setOnSeekBarChangeListener(null)
        }

        private fun showSlider() {
            itemView.operationSlider.visibility = View.VISIBLE
            itemView.operationSliderValue.visibility = View.VISIBLE
        }

        private fun setSliderValue(progress: Int, min: Int) {
            itemView.operationSliderValue.text = (progress + min).toString()
        }
    }

    class ItemCallback : DiffUtil.ItemCallback<Operation>() {

        override fun areItemsTheSame(oldItem: Operation, newItem: Operation): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Operation, newItem: Operation): Boolean {
            return true
        }
    }
}