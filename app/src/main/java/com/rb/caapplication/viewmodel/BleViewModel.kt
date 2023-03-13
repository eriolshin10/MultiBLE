package com.rb.caapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.rb.domain.ble.DeviceEvent
import com.rb.domain.usecase.DeviceConnectionEventUseCase
import com.rb.domain.usecase.ScanBleDevicesUseCase
import com.rb.domain.usecase.TestScanBleDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val scanBleDevicesUseCase: ScanBleDevicesUseCase,
    private val testScanBleDevicesUseCase: TestScanBleDevicesUseCase,
    deviceConnectionEventUseCase: DeviceConnectionEventUseCase
) : ViewModel() {

    val deviceConnectionEvent: SharedFlow<DeviceEvent<Boolean>> =
        deviceConnectionEventUseCase.execute().asSharedFlow()

    fun testScan() = testScanBleDevicesUseCase.execute()
}