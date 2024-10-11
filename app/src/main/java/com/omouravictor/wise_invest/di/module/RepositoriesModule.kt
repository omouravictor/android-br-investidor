package com.omouravictor.wise_invest.di.module

import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.wise_invest.data.remote.api.AlphaVantageApi
import com.omouravictor.wise_invest.data.remote.api.CurrencyExchangeRatesApi
import com.omouravictor.wise_invest.data.remote.api.NewsApi
import com.omouravictor.wise_invest.data.remote.repository.AssetsApiRepository
import com.omouravictor.wise_invest.data.remote.repository.AssetsApiRepositoryImpl
import com.omouravictor.wise_invest.data.remote.repository.CurrencyExchangeRatesApiRepository
import com.omouravictor.wise_invest.data.remote.repository.CurrencyExchangeRatesApiRepositoryImpl
import com.omouravictor.wise_invest.data.remote.repository.FirebaseRepository
import com.omouravictor.wise_invest.data.remote.repository.FirebaseRepositoryImpl
import com.omouravictor.wise_invest.data.remote.repository.NewsApiRepository
import com.omouravictor.wise_invest.data.remote.repository.NewsApiRepositoryImpl
import com.omouravictor.wise_invest.di.model.DispatcherProvider
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
    fun provideAssetsApiRepository(
        dispatchers: DispatcherProvider,
        alphaVantageApi: AlphaVantageApi
    ): AssetsApiRepository = AssetsApiRepositoryImpl(dispatchers, alphaVantageApi)

    @Singleton
    @Provides
    fun provideCurrencyExchangeRatesApiRepository(
        dispatchers: DispatcherProvider,
        currencyExchangeRatesApi: CurrencyExchangeRatesApi
    ): CurrencyExchangeRatesApiRepository =
        CurrencyExchangeRatesApiRepositoryImpl(dispatchers, currencyExchangeRatesApi)

    @Singleton
    @Provides
    fun provideNewsApiRepository(
        dispatchers: DispatcherProvider,
        newsApi: NewsApi
    ): NewsApiRepository = NewsApiRepositoryImpl(dispatchers, newsApi)

    @Singleton
    @Provides
    fun provideFirebaseRepository(
        dispatchers: DispatcherProvider,
        firestore: FirebaseFirestore
    ): FirebaseRepository = FirebaseRepositoryImpl(dispatchers, firestore)

}