package com.rb.domain.ble

import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableSharedFlow

interface BleRepository {

    var deviceConnectionEvent: MutableSharedFlow<DeviceEvent<Boolean>>

    fun scanBleDevice(settings: ScanSettings, scanFilter: ScanFilter): Observable<ScanResult>
    fun connectBleDevice(device: RxBleDevice)
    fun writeData(address: String, sendByteData: ByteArray): Single<ByteArray>?
    fun bleNotification(address: String): Observable<ByteArray>?
    fun disconnectBleDevice(address: String)
    fun testScanBleDevice()
}