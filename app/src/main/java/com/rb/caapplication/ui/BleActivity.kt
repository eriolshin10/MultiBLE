package com.rb.caapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rb.caapplication.R
import com.rb.caapplication.base.BaseActivity
import com.rb.caapplication.databinding.ActivityBleBinding
import com.rb.caapplication.utils.Utils.Companion.repeatOnStarted
import com.rb.caapplication.viewmodel.BleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BleActivity : BaseActivity<ActivityBleBinding, BleViewModel>(R.layout.activity_ble) {

    override val viewModel by viewModels<BleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initVariable() {

    }

    override fun initPermission() {

    }

    override fun initObserver() {
        repeatOnStarted {
            viewModel.deviceConnectionEvent.collect {
                Log.d("sband", "deviceConnectionEvent, deviceName: ${it.deviceName}")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.testScan()
    }


}