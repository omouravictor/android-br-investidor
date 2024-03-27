package com.omouravictor.invest_view.di.module

import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideRemoteAssetsRepository(
        alphaVantageService: AlphaVantageService
    ): RemoteAssetsRepository = RemoteAssetsRepositoryImpl(alphaVantageService)

}