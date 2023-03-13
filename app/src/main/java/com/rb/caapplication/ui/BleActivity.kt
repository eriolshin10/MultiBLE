package com.rb.caapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rb.caapplication.viewmodel.BleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BleActivity : AppCompatActivity() {

    private val viewModel by viewModels<BleViewModel>()

    companion object {
        fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repeatOnStarted {
            viewModel.deviceConnectionEvent.collect {
                Log.d("sband", "deviceConnectionEvent, deviceName : ${it.deviceName}")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.testScan()
    }
}