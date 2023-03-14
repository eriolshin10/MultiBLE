package com.rb.caapplication.viewmodel

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.viewModelScope
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.rb.caapplication.base.BaseViewModel
import com.rb.caapplication.utils.Utils
import com.rb.domain.ble.DeviceEvent
import com.rb.domain.usecase.ConnectBleDeviceUseCase
import com.rb.domain.usecase.DeviceConnectionEventUseCase
import com.rb.domain.usecase.ScanBleDevicesUseCase
import com.rb.domain.usecase.TestScanBleDevicesUseCase
import com.rb.domain.usecase.WriteByteDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class BleViewModel @Inject constructor(
    private val scanBleDevicesUseCase: ScanBleDevicesUseCase,
    private val connectBleDeviceUseCase: ConnectBleDeviceUseCase,
    private val writeByteDataUseCase: WriteByteDataUseCase,
    private val testScanBleDevicesUseCase: TestScanBleDevicesUseCase,
    deviceConnectionEventUseCase: DeviceConnectionEventUseCase
) : BaseViewModel() {

    val deviceConnectionEvent: SharedFlow<DeviceEvent<Boolean>> =
        deviceConnectionEventUseCase.execute().asSharedFlow()

    private var scanSubscription: Disposable? = null

    var scanResults = ObservableArrayMap<String, ScanResult>()
    var connectedDeviceList = ObservableArrayList<String>()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun testScan() = testScanBleDevicesUseCase.execute()

    fun startScan() {
        Log.d("sband", "BleViewModel startScan()")
        val settings: ScanSettings = ScanSettings.Builder().build()
        val scanFilter: ScanFilter = ScanFilter.Builder().build()

        scanResults.clear()

        scanSubscription =
            scanBleDevicesUseCase.execute(settings, scanFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ scanResult ->
                    addScanResult(scanResult)
                }, {throwable ->
                    if (throwable is BleScanException) {
                        Log.d("sband", "BleViewModel BleScanException throwable: ${throwable}")
                    } else {
                        Log.d("sband", "BleViewModel BleScanException Unknown error")
                    }
                })

        Timer("scan",false).schedule(5000L) { stopScan() }
    }

    fun stopScan() {
        scanSubscription?.dispose()
    }

    fun connectBleDevice(device: RxBleDevice) = connectBleDeviceUseCase.execute(device)

    private fun addScanResult(result: ScanResult) {
        val device = result.bleDevice
        val deviceAddress = device.macAddress
        scanResults[deviceAddress] = result
    }

    fun updateConnectedDeviceMap(address: String, flag: Boolean) {
        if (flag) {
            connectedDeviceList.add(address)
            val data = "E015000123" //연결이 끊어지지 않기 위해 보내는 데이터
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                writeData(address, data)
            }

        }
        else {
            connectedDeviceList.remove(address)
        }
    }

    fun writeData(address: String, data: String) {
        val byteData = Utils.hexStringToByteArray(data)

        writeByteDataUseCase.execute(address, byteData)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ bytes ->
                val hexString: String = bytes.joinToString(" ") {
                    String.format("02X", it)
                }
                event(Event.ShowNotification("write `$hexString`.", "success"))
            }, {
                event(Event.ShowNotification("${it.message}", "error"))
            })?.let { addDisposable(it) }

    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    sealed class Event {
        data class ShowNotification(val msg: String, val type: String) : Event()
    }

}