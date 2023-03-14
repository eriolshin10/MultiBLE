package com.rb.caapplication.ui

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.polidea.rxandroidble2.scan.ScanResult

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("adapter")
    fun RecyclerView.bindAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("scanList")
    fun RecyclerView.bindScanList(scanList: ObservableArrayMap<String, ScanResult>) {
        val adapter = this.adapter
        if (adapter is ScanAdapter) {
            adapter.submitList(scanList.values.toList())
        }
    }

    @JvmStatic
    @BindingAdapter("connectedDeviceList")
    fun RecyclerView.bindConnectedDeviceList(deviceList: MutableList<String>) {
        val adapter = this.adapter
        if (adapter is ConnectedDeviceAdapter) {
            adapter.submitList(deviceList.toList())
        }
    }

}