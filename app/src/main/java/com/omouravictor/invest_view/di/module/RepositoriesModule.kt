package com.omouravictor.invest_view.di.module

import com.omouravictor.invest_view.data.network.alpha_vantage.ApiService
import com.omouravictor.invest_view.data.repository.AssetsRepository
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
    fun provideRatesRepository(
        apiService: ApiService
    ): AssetsRepository = AssetsRepository(apiService)

}