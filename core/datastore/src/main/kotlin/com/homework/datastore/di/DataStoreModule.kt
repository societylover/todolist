package com.homework.datastore.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.homework.datastore.ApiParamsProvider
import com.homework.datastore.ApiParamsProviderImpl
import com.homework.datastore.ApiParamsProviderImpl.Companion.API_PREFERENCES
import com.homework.datastore.DeviceParams
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DeviceSensitive

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providerApiParamsDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext, API_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(API_PREFERENCES) })


    @SuppressLint("HardwareIds")
    @Singleton
    @Provides
    fun provideApiParamsProvider(
        @DeviceSensitive deviceParams: DeviceParams,
        dataStore: DataStore<Preferences>,
    ): ApiParamsProvider =
        ApiParamsProviderImpl(dataStore = dataStore, deviceParams = deviceParams)

    @Provides
    @DeviceSensitive
    fun provideDeviceId(@ApplicationContext context: Context): DeviceParams {
        return DeviceParams(context)
    }
}