package com.rb.caapplication.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.rb.caapplication.R
import com.rb.caapplication.base.BaseActivity
import com.rb.caapplication.databinding.ActivityBleBinding
import com.rb.caapplication.utils.Utils
import com.rb.caapplication.utils.Utils.Companion.repeatOnStarted
import com.rb.caapplication.viewmodel.BleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BleActivity : BaseActivity<ActivityBleBinding, BleViewModel>(R.layout.activity_ble) {

    override val viewModel by viewModels<BleViewModel>()

    private val scanAdapter: ScanAdapter by lazy {
        ScanAdapter { scanResult ->
            Log.d("sband", "ScanAdapter 아이템 클릭 bleDevice: ${scanResult.bleDevice.macAddress}")
            viewModel.connectBleDevice(scanResult.bleDevice)
        }
    }

    private val connectedDeviceAdapter: ConnectedDeviceAdapter by lazy {
        ConnectedDeviceAdapter { address, data ->
            if (data == null) viewModel.disconnectBleDevice(address)
            else viewModel.writeData(address, data)
        }
    }

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val PERMISSIONS_S_ABOVE = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val LOCATION_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val REQUEST_ALL_PERMISSION = 1
        val REQUEST_LOCATION_PERMISION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initVariable() {
        binding.viewmodel = viewModel
        binding.scanAdapter = scanAdapter
        binding.connectedDeviceAdapter = connectedDeviceAdapter
    }

    override fun initPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (!hasPermissions(this, PERMISSIONS_S_ABOVE)) {
                requestPermissions(PERMISSIONS_S_ABOVE, REQUEST_ALL_PERMISSION)
            }
        } else {
            if (!hasPermissions(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
            }
        }
    }

    override fun initObserver() {
        repeatOnStarted {
            viewModel.eventFlow.collect{ event ->
                handleEvent(event)
            }
        }
        repeatOnStarted {
            viewModel.deviceConnectionEvent.collect {
                Log.d("sband", "BleActivity DeviceConnectionEvent collect, address: ${it.address}\t${it.data}")
                viewModel.updateConnectedDeviceMap(it.address, it.data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.testScan()
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            //requestPermissions(permissions, LOCATION_PERMISSION)
            Toast.makeText(this, "Permissions must be granted!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun handleEvent(event: BleViewModel.Event) = when (event) {
        is BleViewModel.Event.BleScanException -> {

        }
        is BleViewModel.Event.ShowNotification -> {
            Utils.showNotification(event.msg,event.type)
        }
        is BleViewModel.Event.NotifyData -> {
            Log.d("sband", "BleActivity NotifyData address: ${event.address}\tdata: ${event.data}")
        }
    }


}