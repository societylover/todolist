package com.homework.todolist.di

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
import com.homework.todolist.data.userpreferences.UserPreferencesProvider
import com.homework.todolist.data.userpreferences.UserPreferencesProviderImpl
import com.homework.todolist.data.userpreferences.UserPreferencesProviderImpl.Companion.USER_PREFERENCES
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

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApiParamsPreferences

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class UserAppPreferences

@Module
@InstallIn(SingletonComponent::class)
object TodosModule {

    @Provides
    @Singleton
    fun provideTodoItemsRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSourceImpl,
        @ApplicationScope externalScope: CoroutineScope
    ): TodoItemsRepository =
        TodoItemsRepositoryImpl(localDataSource, remoteDataSource, externalScope)

    @ApiParamsPreferences
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

    @UserAppPreferences
    @Provides
    @Singleton
    fun provideUserAppPreferences(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext, USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) })


    @Singleton
    @Provides
    fun provideApiParamsProvider(
        @DeviceSensitive deviceParams: DeviceParams,
        @ApiParamsPreferences dataStore: DataStore<Preferences>,
    ): ApiParamsProvider =
        ApiParamsProviderImpl(dataStore = dataStore, deviceParams = deviceParams)

    @Singleton
    @Provides
    fun provideUserPreferencesProvider(
        @UserAppPreferences dataStore: DataStore<Preferences>,
    ): UserPreferencesProvider =
        UserPreferencesProviderImpl(dataStore = dataStore)

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