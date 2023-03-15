package com.rb.domain.usecase

import com.rb.domain.ble.BleRepository
import javax.inject.Inject

class NotifyUseCase @Inject constructor(private val repository: BleRepository){

    fun execute(address: String) = repository.bleNotification(address)

}