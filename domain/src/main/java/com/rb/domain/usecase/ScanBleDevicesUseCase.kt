package com.rb.domain.usecase

import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import com.rb.domain.ble.BleRepository
import javax.inject.Inject

class ScanBleDevicesUseCase @Inject constructor(private val repository: BleRepository) {
    fun execute(scanSettings: ScanSettings, scanFilter: ScanFilter)
            = repository.scanBleDevice(scanSettings, scanFilter)
}