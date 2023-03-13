package com.rb.domain.usecase

import com.polidea.rxandroidble2.RxBleDevice
import com.rb.domain.ble.BleRepository
import javax.inject.Inject

class ConnectBleDeviceUseCase @Inject constructor(private val repository: BleRepository) {

    fun execute(device: RxBleDevice) = repository.connectBleDevice(device)

}