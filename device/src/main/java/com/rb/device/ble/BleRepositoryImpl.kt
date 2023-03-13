package com.rb.device.ble

import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.rb.domain.ble.BleRepository
import com.rb.domain.ble.DeviceEvent
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class BleRepositoryImpl(private val rxBleClient: RxBleClient) : BleRepository {

    private var rxBleConnectionMap = hashMapOf<String, RxBleConnection>()
    private var consStateDisposableMap = hashMapOf<String, Disposable>()
    private var connectSubscriptionMap = hashMapOf<String, Disposable>()
    private var connectDeviceMap = hashMapOf<String, RxBleDevice>()

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

    override fun connectBleDevice(device: RxBleDevice) {
        consStateDisposableMap[device.macAddress] = device.observeConnectionStateChanges()
            .subscribe(
                { connectState ->
                    connectionStateListener(device, connectState)
                }
            ) { throwable ->
                throwable.printStackTrace()
            }
        connectSubscriptionMap[device.macAddress] = device.establishConnection(false)
            .flatMapSingle { _rxBleConnection ->
                rxBleConnectionMap[device.macAddress] = _rxBleConnection
                _rxBleConnection.discoverServices()
            }.subscribe({

            }, {

            })
    }

    private fun connectionStateListener(
        device: RxBleDevice,
        connectionState: RxBleConnection.RxBleConnectionState
    ) {
        when(connectionState) {
            RxBleConnection.RxBleConnectionState.CONNECTED -> {
                Log.d("sband", "BleRepositoryImpl connectionStateListener() CONNECTED device: ${device.macAddress}, Thread: ${Thread.currentThread().name}")
                CoroutineScope(Dispatchers.IO).launch {
                    deviceConnectionEvent.emit(DeviceEvent.deviceConnectionEvent(device.macAddress, true))
                }
            }
            RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                consStateDisposableMap[device.macAddress]?.dispose()
                CoroutineScope(Dispatchers.IO).launch {
                    deviceConnectionEvent.emit(DeviceEvent.deviceConnectionEvent(device.macAddress, false))
                    connectDeviceMap.remove(device.macAddress) //TODO 없어도 될 듯?
                }
            }
            RxBleConnection.RxBleConnectionState.CONNECTING -> {}
            RxBleConnection.RxBleConnectionState.DISCONNECTING -> {}
        }
    }
}