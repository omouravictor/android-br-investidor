package com.omouravictor.invest_view.framework.di

import com.omouravictor.invest_view.data.network.hgfinanceapi.ApiService
import com.omouravictor.invest_view.data.repository.RatesRepository
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
    ): RatesRepository = RatesRepository(apiService)

}