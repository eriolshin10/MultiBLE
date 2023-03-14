package com.rb.caapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rb.caapplication.R
import com.rb.caapplication.databinding.ItemConnectedDeviceBinding

class ConnectedDeviceAdapter(
    private val itemClicked: (String) -> Unit
) : ListAdapter<String, ConnectedDeviceAdapter.ConnectedDeviceViewHolder>(diffUtil) {

    class ConnectedDeviceViewHolder(
        private val binding: ItemConnectedDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: String) {
            binding.address.text = address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectedDeviceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemConnectedDeviceBinding>(layoutInflater, R.layout.item_connected_device, parent, false)

        return ConnectedDeviceViewHolder(binding).apply {
            binding.send.setOnClickListener {
                val position = adapterPosition
                itemClicked(getItem(position))
            }
        }

    }

    override fun onBindViewHolder(holder: ConnectedDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }



}
