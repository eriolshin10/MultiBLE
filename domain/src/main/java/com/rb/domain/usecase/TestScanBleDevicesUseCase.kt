package com.rb.domain.usecase

import com.rb.domain.ble.BleRepository
import javax.inject.Inject

class TestScanBleDevicesUseCase @Inject constructor(private val repository: BleRepository) {
    fun execute() = repository.testScanBleDevice()
}