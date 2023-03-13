package com.rb.domain.ble

class DeviceEvent<out T> private constructor(val deviceName: String, val data: T) {
    companion object{
        fun <T> deviceConnectionEvent(deviceName: String, data: T) = DeviceEvent(deviceName, data)
    }
}