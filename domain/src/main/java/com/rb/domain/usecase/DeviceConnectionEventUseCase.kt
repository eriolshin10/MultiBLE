package com.rb.domain.usecase

import com.rb.domain.ble.BleRepository
import com.rb.domain.ble.DeviceEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class DeviceConnectionEventUseCase @Inject constructor(private val repository: BleRepository) {
    fun execute(): MutableSharedFlow<DeviceEvent<Boolean>> = repository.deviceConnectionEvent
}