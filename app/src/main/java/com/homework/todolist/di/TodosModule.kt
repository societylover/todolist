package com.homework.todolist.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.homework.todolist.data.datasource.local.LocalDataSource
import com.homework.todolist.data.datasource.local.TodoDatabase
import com.homework.todolist.data.datasource.local.TodoItemDao
import com.homework.todolist.data.datasource.remote.RemoteDataSourceImpl
import com.homework.todolist.data.provider.ApiParamsProvider
import com.homework.todolist.data.provider.ApiParamsProviderImpl
import com.homework.todolist.data.provider.ApiParamsProviderImpl.Companion.API_PREFERENCES
import com.homework.todolist.data.provider.DeviceParams
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.data.repository.TodoItemsRepositoryImpl
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
object TodosModule {

    @Provides
    @Singleton
    fun provideTodoItemsRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSourceImpl
    ): TodoItemsRepository =
        TodoItemsRepositoryImpl(localDataSource, remoteDataSource)

    @Provides
    @Singleton
    fun providerApiParamsProvider(@ApplicationContext appContext: Context): DataStore<Preferences> =
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): TodoDatabase {
        return Room.databaseBuilder(
            appContext,
            TodoDatabase::class.java,
            "todo_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(todoDatabase: TodoDatabase) : TodoItemDao {
        return todoDatabase.todoItemDao()
    }

}