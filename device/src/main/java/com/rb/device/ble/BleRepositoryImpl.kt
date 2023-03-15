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
import io.reactivex.Single
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
                CoroutineScope(Dispatchers.IO).launch {
                    deviceConnectionEvent.emit(DeviceEvent.deviceConnectionEvent(device.macAddress, true))
                }
//                Log.d("sband", "BleRepositoryImpl rxBleConnectionMap.size: ${rxBleConnectionMap.size
//                }\tconsStateDisposableMap.size: ${consStateDisposableMap.size}\tconnectSubscriptionMap.size: ${connectSubscriptionMap.size}" )
            }
            RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                consStateDisposableMap[device.macAddress]?.dispose()
                connectSubscriptionMap[device.macAddress]?.dispose()
                CoroutineScope(Dispatchers.IO).launch {
                    deviceConnectionEvent.emit(DeviceEvent.deviceConnectionEvent(device.macAddress, false))
                }
                rxBleConnectionMap.remove(device.macAddress)
                consStateDisposableMap.remove(device.macAddress)
                connectSubscriptionMap.remove(device.macAddress)
//                Log.d("sband", "BleRepositoryImpl rxBleConnectionMap.size: ${rxBleConnectionMap.size
//                }\tconsStateDisposableMap.size: ${consStateDisposableMap.size}\tconnectSubscriptionMap.size: ${connectSubscriptionMap.size}" )
            }
            RxBleConnection.RxBleConnectionState.CONNECTING -> {}
            RxBleConnection.RxBleConnectionState.DISCONNECTING -> {}
        }
    }

    override fun writeData(address: String, sendByteData: ByteArray): Single<ByteArray>? {
        return rxBleConnectionMap[address]?.writeCharacteristic(
            BleConst.UUID_SBAND_NOTIFY_DATA,
            sendByteData
        )
    }

    override fun bleNotification(address: String): Observable<ByteArray>? {
        return rxBleConnectionMap[address]
            ?.setupNotification(BleConst.UUID_SBAND_NOTIFY_DATA)
            ?.doOnNext {  notificationObservable ->

            }
            ?.flatMap { notificationObservable -> notificationObservable }
    }

    override fun disconnectBleDevice(address: String) {
        connectSubscriptionMap[address]?.dispose()
    }
}