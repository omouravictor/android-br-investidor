package com.omouravictor.invest_view.di.module

import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.data.network.remote.repository.AssetsApiRepositoryImpl
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepositoryImpl
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
        alphaVantageService: AlphaVantageService
    ): AssetsApiRepository = AssetsApiRepositoryImpl(alphaVantageService)

    @Singleton
    @Provides
    fun provideFirebaseRepository(
        firestore: FirebaseFirestore
    ): FirebaseRepository = FirebaseRepositoryImpl(firestore)

}