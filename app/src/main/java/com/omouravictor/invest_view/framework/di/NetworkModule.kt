package com.omouravictor.invest_view.framework.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

//    private const val TIMEOUT_SECONDS = 15L
//
//    @Singleton
//    @Provides
//    fun provideRetrofit(
//        provideGsonConverterFactory: GsonConverterFactory,
//        provideOkHttpClient: OkHttpClient
//    ): ApiService {
//        return Retrofit.Builder()
//            .baseUrl(BuildConfig.BASE_URL_HG_FINANCE)
//            .client(provideOkHttpClient)
//            .addConverterFactory(provideGsonConverterFactory)
//            .build()
//            .create(ApiService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideGsonConverterFactory(): GsonConverterFactory {
//        return GsonConverterFactory.create()
//    }
//
//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        provideAuthorizationInterceptor: AuthorizationInterceptor,
//        provideLoggingInterceptor: HttpLoggingInterceptor
//    ): OkHttpClient {
//        return OkHttpClient().newBuilder()
//            .addInterceptor(provideLoggingInterceptor)
//            .addInterceptor(provideAuthorizationInterceptor)
//            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAuthorizationInterceptor(): AuthorizationInterceptor {
//        return AuthorizationInterceptor(
//            apiKey = BuildConfig.API_KEY_HG_FINANCE
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
//        return HttpLoggingInterceptor().apply {
//            setLevel(
//                if (BuildConfig.DEBUG)
//                    HttpLoggingInterceptor.Level.BODY
//                else
//                    HttpLoggingInterceptor.Level.NONE
//            )
//        }
//    }

}