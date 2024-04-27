package com.omouravictor.invest_view.presenter.wallet.assets

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val currentAssets get() = _assetsList.value.orEmpty()
    val currentAssetTypes get() = currentAssets.map { it.assetType }.distinct()
    private val _assetsList = MutableLiveData<List<AssetUiModel>>()
    val assetsList: LiveData<List<AssetUiModel>> = _assetsList

    init {
        val testList = listOf(
            AssetUiModel(
                symbol = "AAPL",
                name = "Apple Inc.",
                assetType = AssetTypes.STOCK,
                region = "United States",
                currency = "USD",
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "AAPL34",
                name = "Apple Inc.",
                assetType = AssetTypes.BDR,
                region = "Brazil",
                currency = "BRL",
                price = 50.0,
                amount = 10,
                totalInvested = 1000.0
            ),
            AssetUiModel(
                symbol = "AAPL",
                name = "Apple Inc.",
                assetType = AssetTypes.STOCK,
                region = "United States",
                currency = "USD",
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "MSTFT",
                name = "Apple Inc.",
                assetType = AssetTypes.ETF,
                region = "Germany",
                currency = "EUR",
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "VALE3",
                name = "Vale S.A.",
                assetType = AssetTypes.LOCAL_STOCK,
                region = "Brasil",
                currency = "BRL",
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "VALE3",
                name = "Vale S.A.",
                assetType = AssetTypes.OTHER,
                region = "Brasil",
                currency = "BRL",
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            )
        )
        _assetsList.value = testList
    }

    fun addAsset(assetUiModel: AssetUiModel) {
        val currentList = currentAssets.toMutableList()
        currentList.add(assetUiModel)
        _assetsList.value = currentList
    }

}