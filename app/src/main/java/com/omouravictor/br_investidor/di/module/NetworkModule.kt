package com.omouravictor.br_investidor.di.module

import android.content.Context
import com.omouravictor.br_investidor.BuildConfig
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.AlphaVantageApi
import com.omouravictor.br_investidor.data.remote.apis.free_currency_api.CurrencyExchangeRatesApi
import com.omouravictor.br_investidor.data.remote.apis.news_api.NewsApi
import com.omouravictor.br_investidor.di.model.RequestCacheInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val timeout = 10L
    private val timeUnit = TimeUnit.SECONDS

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
    fun provideStocksApi(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): AlphaVantageApi {
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("X-RapidAPI-Key", BuildConfig.STOCKS_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(getCacheForOkHttpClient(context, "stocks_api_cache"))
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(RequestCacheInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .build()

        val retrofit = createRetrofit(BuildConfig.STOCKS_API_BASE_URL, okHttpClient)

        return retrofit.create(AlphaVantageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyExchangeRatesApi(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): CurrencyExchangeRatesApi {
        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter("apikey", BuildConfig.CURRENCY_EXCHANGE_RATES_API_KEY)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(getCacheForOkHttpClient(context, "currency_exchange_rates_api_cache"))
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(RequestCacheInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .build()

        val retrofit = createRetrofit(BuildConfig.CURRENCY_EXCHANGE_RATES_API_BASE_URL, okHttpClient)

        return retrofit.create(CurrencyExchangeRatesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsApi(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): NewsApi {
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("x-api-key", BuildConfig.NEWS_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(getCacheForOkHttpClient(context, "news_api_cache"))
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(RequestCacheInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .build()

        val retrofit = createRetrofit(BuildConfig.NEWS_API_BASE_URL, okHttpClient)

        return retrofit.create(NewsApi::class.java)
    }

    private fun createRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getCacheForOkHttpClient(context: Context, childReference: String, cacheSizeMB: Long = 10): Cache {
        return Cache(File(context.cacheDir, childReference), cacheSizeMB * 1024 * 1024)
    }

}