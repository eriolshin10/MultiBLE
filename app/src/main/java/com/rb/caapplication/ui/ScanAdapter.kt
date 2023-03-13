package com.rb.caapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.polidea.rxandroidble2.scan.ScanResult
import com.rb.caapplication.R
import com.rb.caapplication.databinding.ItemScanBinding

class ScanAdapter(
    private val itemClicked: (ScanResult) -> Unit
) : ListAdapter<ScanResult, ScanAdapter.ScanViewHolder>(diffUtil) {

    class ScanViewHolder(
        private val binding: ItemScanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(scanResult: ScanResult) {
            binding.name.text = scanResult.bleDevice.name
            binding.address.text = scanResult.bleDevice.macAddress
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemScanBinding>(layoutInflater, R.layout.item_scan, parent, false)

        return ScanViewHolder(binding).apply {
            binding.connect.setOnClickListener {
                val position = adapterPosition
                itemClicked(getItem(position))
            }
        }
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ScanResult>() {
            override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
                return oldItem.bleDevice.name == newItem.bleDevice.name
            }

            override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
                return oldItem.bleDevice.macAddress == newItem.bleDevice.macAddress
            }
        }
    }

}