package net.activelook.sdk.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_operations.*
import kotlinx.android.synthetic.main.list_item_operation_click.view.*
import kotlinx.android.synthetic.main.list_item_operation_switch.view.*
import net.activelook.sdk.ActiveLookSdk
import net.activelook.sdk.operation.ActiveLookOperation

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
    val onPlay: (checked: Boolean) -> Unit
) : Operation

class OperationListAdapter :
    ListAdapter<Operation, OperationListAdapter.OperationViewHolder>(ItemCallback()) {

    companion object {
        private const val TYPE_CLICK = 1
        private const val TYPE_SWITCH = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OperationClick -> TYPE_CLICK
            is OperationSwitch -> TYPE_SWITCH
            else -> TYPE_CLICK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        return when (viewType) {
            TYPE_CLICK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_operation_click, parent, false)
                OperationClickViewHolder(view)
            }
            TYPE_SWITCH -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_operation_switch, parent, false)
                OperationSwitchViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_operation_click, parent, false)
                OperationClickViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        val operation = getItem(position)
        when (holder) {
            is OperationClickViewHolder -> holder.bind(operation as OperationClick)
            is OperationSwitchViewHolder -> holder.bind(operation as OperationSwitch)
        }
    }

    abstract class OperationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class OperationClickViewHolder(itemView: View) : OperationViewHolder(itemView) {

        fun bind(operation: OperationClick) {
            itemView.operationClickLabel.text = operation.name
            itemView.playOperationClickImage.setOnClickListener { operation.onPlay() }
        }

    }

    class OperationSwitchViewHolder(itemView: View) : OperationViewHolder(itemView) {

        fun bind(operation: OperationSwitch) {
            itemView.operationSwitchLabel.text = operation.name
            itemView.operationSwitch.isChecked = operation.defaultValue
            itemView.playOperationSwitchImage.setOnClickListener {
                val isChecked = itemView.operationSwitch.isChecked
                operation.onPlay(isChecked)
            }
        }

    }

    class ItemCallback: DiffUtil.ItemCallback<Operation>() {

        override fun areItemsTheSame(oldItem: Operation, newItem: Operation): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Operation, newItem: Operation): Boolean {
            return true
        }
    }
}