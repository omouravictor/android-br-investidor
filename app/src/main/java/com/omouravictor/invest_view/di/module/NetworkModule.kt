package com.omouravictor.invest_view.di.module

import com.omouravictor.invest_view.BuildConfig
import com.omouravictor.invest_view.data.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.remote.api.NewsService
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
    fun provideAlphaVantageService(httpLoggingInterceptor: HttpLoggingInterceptor): AlphaVantageService {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("X-RapidAPI-Key", BuildConfig.ALPHA_VANTAGE_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = createOkHttpClient(interceptor, httpLoggingInterceptor)
        val retrofit = createRetrofit(BuildConfig.ALPHA_VANTAGE_API_BASE_URL, okHttpClient)

        return retrofit.create(AlphaVantageService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsService(httpLoggingInterceptor: HttpLoggingInterceptor): NewsService {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("apiKey", BuildConfig.NEWS_API_KEY)
                .build()
            chain.proceed(request)
        }

        val okHttpClient = createOkHttpClient(interceptor, httpLoggingInterceptor)
        val retrofit = createRetrofit(BuildConfig.NEWS_API_BASE_URL, okHttpClient)

        return retrofit.create(NewsService::class.java)
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