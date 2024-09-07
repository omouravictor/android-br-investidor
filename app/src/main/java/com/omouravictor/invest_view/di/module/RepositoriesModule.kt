package com.omouravictor.invest_view.di.module

import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.data.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.data.remote.repository.AssetsApiRepositoryImpl
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepositoryImpl
import com.omouravictor.invest_view.di.model.DispatcherProvider
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
        alphaVantageService: AlphaVantageService
    ): AssetsApiRepository = AssetsApiRepositoryImpl(dispatchers, alphaVantageService)

    @Singleton
    @Provides
    fun provideFirebaseRepository(
        dispatchers: DispatcherProvider,
        firestore: FirebaseFirestore
    ): FirebaseRepository = FirebaseRepositoryImpl(dispatchers, firestore)

}