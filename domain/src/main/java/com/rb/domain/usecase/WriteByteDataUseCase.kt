package com.rb.domain.usecase

import com.rb.domain.ble.BleRepository
import javax.inject.Inject

class WriteByteDataUseCase @Inject constructor(private val repository: BleRepository) {

    fun execute(address: String, sendByteArray: ByteArray) = repository.writeData(address, sendByteArray)

}