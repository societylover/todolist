package com.homework.network.di

import com.homework.common.network.AppDispatchers
import com.homework.common.network.Dispatcher
import com.homework.common.network.di.BackgroundScope
import com.homework.common.network.di.ServiceUrl
import com.homework.datastore.ApiParamsProvider
import com.homework.network.RemoteDataSource
import com.homework.network.ktor.KtorNetwork
import com.homework.network.ktor.RemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    fun provideTodoServiceClient(
        apiParamsProvider: ApiParamsProvider,
        @ServiceUrl baseUrl: String
    ): HttpClient = KtorNetwork(apiParamsProvider, baseUrl).client

    @Provides
    fun provideRemoteDataSource(
        @Dispatcher(AppDispatchers.IO) dispatcher: CoroutineDispatcher,
        @BackgroundScope externalScope: CoroutineScope,
        client: HttpClient
    ): RemoteDataSource = RemoteDataSourceImpl(dispatcher, client)
}