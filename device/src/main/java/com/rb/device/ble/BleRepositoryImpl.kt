package com.rb.device.ble

import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.rb.domain.ble.BleRepository
import com.rb.domain.ble.DeviceEvent
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class BleRepositoryImpl(private val rxBleClient: RxBleClient) : BleRepository {

    override var deviceConnectionEvent = MutableSharedFlow<DeviceEvent<Boolean>>()

    override fun scanBleDevice(
        settings: ScanSettings,
        scanFilter: ScanFilter
    ): Observable<ScanResult> = rxBleClient.scanBleDevices(settings, scanFilter)

    override fun testScanBleDevice() {
        CoroutineScope(Dispatchers.IO).launch {
            deviceConnectionEvent.emit(DeviceEvent.deviceConnectionEvent("테스트 유즈케이스2", true))
        }
    }
}