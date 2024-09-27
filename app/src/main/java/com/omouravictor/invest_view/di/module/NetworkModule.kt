package com.omouravictor.invest_view.di.module

import com.omouravictor.invest_view.BuildConfig
import com.omouravictor.invest_view.data.remote.api.AlphaVantageApi
import com.omouravictor.invest_view.data.remote.api.CurrencyExchangeRatesApi
import com.omouravictor.invest_view.data.remote.api.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideAlphaVantageApi(httpLoggingInterceptor: HttpLoggingInterceptor): AlphaVantageApi {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("X-RapidAPI-Key", BuildConfig.ALPHA_VANTAGE_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = createOkHttpClient(interceptor, httpLoggingInterceptor)
        val retrofit = createRetrofit(BuildConfig.ALPHA_VANTAGE_API_BASE_URL, okHttpClient)

        return retrofit.create(AlphaVantageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyExchangeRatesApi(httpLoggingInterceptor: HttpLoggingInterceptor): CurrencyExchangeRatesApi {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("X-RapidAPI-Key", BuildConfig.CURRENCY_EXCHANGE_RATES_API_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = createOkHttpClient(interceptor, httpLoggingInterceptor)
        val retrofit = createRetrofit(BuildConfig.CURRENCY_EXCHANGE_RATES_API_BASE_URL, okHttpClient)

        return retrofit.create(CurrencyExchangeRatesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsApi(httpLoggingInterceptor: HttpLoggingInterceptor): NewsApi {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("x-api-key", BuildConfig.NEWS_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = createOkHttpClient(interceptor, httpLoggingInterceptor)
        val retrofit = createRetrofit(BuildConfig.NEWS_API_BASE_URL, okHttpClient)

        return retrofit.create(NewsApi::class.java)
    }

    private fun createOkHttpClient(
        interceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        timeout: Long = 10,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .build()
    }

    private fun createRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}