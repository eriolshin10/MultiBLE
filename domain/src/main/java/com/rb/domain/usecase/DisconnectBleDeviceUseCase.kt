package com.rb.domain.usecase

import com.rb.domain.ble.BleRepository
import javax.inject.Inject

class DisconnectBleDeviceUseCase @Inject constructor(private val repository: BleRepository) {

    fun execute(address: String) = repository.disconnectBleDevice(address)

}