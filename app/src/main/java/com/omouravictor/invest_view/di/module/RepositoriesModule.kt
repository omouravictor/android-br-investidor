package com.omouravictor.invest_view.di.module

import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.repository.AssetQuoteRepository
import com.omouravictor.invest_view.data.network.remote.repository.AssetQuoteRepositoryImpl
import com.omouravictor.invest_view.data.network.remote.repository.AssetsBySearchRepository
import com.omouravictor.invest_view.data.network.remote.repository.AssetsBySearchRepositoryImpl
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
    fun provideAssetsBySearchRepository(
        alphaVantageService: AlphaVantageService
    ): AssetsBySearchRepository = AssetsBySearchRepositoryImpl(alphaVantageService)

    @Singleton
    @Provides
    fun provideAssetQuoteRepository(
        alphaVantageService: AlphaVantageService
    ): AssetQuoteRepository = AssetQuoteRepositoryImpl(alphaVantageService)

}