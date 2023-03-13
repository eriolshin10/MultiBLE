package com.rb.caapplication.di

import com.polidea.rxandroidble2.RxBleClient
import com.rb.device.ble.BleRepositoryImpl
import com.rb.domain.ble.BleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBleRepository(rxBleClient: RxBleClient): BleRepository {
        return BleRepositoryImpl(rxBleClient)
    }
}