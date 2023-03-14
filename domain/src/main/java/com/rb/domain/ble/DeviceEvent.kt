package com.rb.domain.ble

class DeviceEvent<out T> private constructor(val address: String, val data: T) {
    companion object{
        fun <T> deviceConnectionEvent(address: String, data: T) = DeviceEvent(address, data)
    }
}