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

import kotlinx.android.synthetic.main.list_item_operation.view.*
import net.activelook.sdk.ActiveLookSdk

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
            Operation(getString(R.string.operation_hello)) {
                ActiveLookSdk.shared.sayHello()
            },
            Operation(getString(R.string.operation_battery)) {
                ActiveLookSdk.shared.getBattery()
            },
            Operation(getString(R.string.operation_clear)) {
                ActiveLookSdk.shared.clearScreen()
            }
        )

        adapter.submitList(operations)
    }
}

data class Operation(val name: String, val onPlay: () -> Unit)

class OperationListAdapter : ListAdapter<Operation, OperationListAdapter.OperationListViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_operation, parent, false)
        val vh = OperationListViewHolder(view)
        return vh
    }

    override fun onBindViewHolder(holder: OperationListViewHolder, position: Int) {
        val device = getItem(position)
        holder.bindDevice(device)
    }

    class OperationListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindDevice(operation: Operation) {
            itemView.operationLabel.text = operation.name
            itemView.setOnClickListener { operation.onPlay() }
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