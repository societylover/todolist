package com.homework.todolist.di

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.homework.todolist.data.datasource.local.LocalDataSource
import com.homework.todolist.data.datasource.remote.RemoteDataSourceImpl
import com.homework.todolist.data.provider.ApiParamsProvider
import com.homework.todolist.data.provider.ApiParamsProviderImpl
import com.homework.todolist.data.provider.ApiParamsProviderImpl.Companion.API_PREFERENCES
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
import javax.inject.Singleton

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
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
        ) : ApiParamsProvider =
        ApiParamsProviderImpl(
            dataStore = dataStore,
            androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
}