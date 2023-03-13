package com.rb.caapplication.viewmodel

import android.util.Log
import androidx.databinding.ObservableArrayMap
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.rb.caapplication.base.BaseViewModel
import com.rb.domain.ble.DeviceEvent
import com.rb.domain.usecase.DeviceConnectionEventUseCase
import com.rb.domain.usecase.ScanBleDevicesUseCase
import com.rb.domain.usecase.TestScanBleDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class BleViewModel @Inject constructor(
    private val scanBleDevicesUseCase: ScanBleDevicesUseCase,
    private val testScanBleDevicesUseCase: TestScanBleDevicesUseCase,
    deviceConnectionEventUseCase: DeviceConnectionEventUseCase
) : BaseViewModel() {

    val deviceConnectionEvent: SharedFlow<DeviceEvent<Boolean>> =
        deviceConnectionEventUseCase.execute().asSharedFlow()

    private var scanSubscription: Disposable? = null

    var scanResults = ObservableArrayMap<String, ScanResult>()


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

    private fun addScanResult(result: ScanResult) {
        val device = result.bleDevice
        val deviceAddress = device.macAddress
        scanResults[deviceAddress] = result
    }

}