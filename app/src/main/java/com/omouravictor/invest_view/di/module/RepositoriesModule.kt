package com.omouravictor.invest_view.di.module

import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetQuoteRepository
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetQuoteRepositoryImpl
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsBySearchRepository
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsBySearchRepositoryImpl
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
    fun provideRemoteAssetsBySearchRepository(
        alphaVantageService: AlphaVantageService
    ): RemoteAssetsBySearchRepository = RemoteAssetsBySearchRepositoryImpl(alphaVantageService)

    @Singleton
    @Provides
    fun provideRemoteAssetQuoteRepository(
        alphaVantageService: AlphaVantageService
    ): RemoteAssetQuoteRepository = RemoteAssetQuoteRepositoryImpl(alphaVantageService)

}