package net.activelook.sdk.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_operation.view.*

class OperationListAdapter: ListAdapter<Operation, OperationListAdapter.OperationViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_operation, parent, false)
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
                }
                is OperationSwitch -> {
                    showSwitch()
                    hideSlider()
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
                }
            }

            initClickListener(operation)
        }

        private fun initClickListener(operation: Operation) {
            val onClick: (View) -> Unit = when (operation) {
                is OperationClick -> {
                    {
                        operation.onPlay()
                    }
                }
                is OperationSwitch -> {
                    {
                        val isChecked = itemView.operationSwitch.isChecked
                        operation.onPlay(isChecked)
                    }
                }
                is OperationSliderAndSwitch -> {
                    {
                        val progress = itemView.operationSlider.progress + operation.min
                        val isChecked = itemView.operationSwitch.isChecked
                        operation.onPlay(progress, isChecked)
                    }
                }
                else -> {
                    {

                    }
                }
            }

            itemView.operationPlay.setOnClickListener(onClick)
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