package com.omouravictor.invest_view.presenter.wallet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.AssetTypes
import com.omouravictor.invest_view.presenter.base.Currencies
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsLiveData = MutableLiveData<List<AssetUiModel>>()
    val assetsLiveData: LiveData<List<AssetUiModel>> = _assetsLiveData
    val assetsList get() = assetsLiveData.value.orEmpty()
    val assetTypesList get() = assetsList.map { it.assetType }.distinct()
    val assetCurrenciesList get() = assetsList.map { it.currency }.distinct()
    val assetRegionsList get() = assetsList.map { it.region }.distinct()

    init {
        val testList = listOf(
            AssetUiModel(
                symbol = "AAPL",
                name = "Apple Inc.",
                assetType = AssetTypes.STOCK,
                region = "United States",
                currency = Currencies.USD,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "AAPL34",
                name = "Apple Inc.",
                assetType = AssetTypes.BDR,
                region = "Brazil/Sao Paolo",
                currency = Currencies.BRL,
                price = 50.0,
                amount = 10,
                totalInvested = 1000.0
            ),
            AssetUiModel(
                symbol = "MSFT",
                name = "Apple Inc.",
                assetType = AssetTypes.ETF,
                region = "Amsterdam",
                currency = Currencies.EUR,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "VALE3",
                name = "Vale S.A.",
                assetType = AssetTypes.BRAZILIAN_STOCK,
                region = "Brazil/Sao Paolo",
                currency = Currencies.BRL,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "MXRF11",
                name = "Max renda.",
                assetType = AssetTypes.FI,
                region = "Brazil/Sao Paolo",
                currency = Currencies.BRL,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "ASDAS",
                name = "Vale S.A.",
                assetType = AssetTypes.OTHER,
                region = "Brazil/Sao Paolo",
                currency = Currencies.BRL,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            )
        )
        _assetsLiveData.value = testList
    }

    fun addAsset(assetUiModel: AssetUiModel) {
        val currentList = assetsList.toMutableList()
        currentList.add(assetUiModel)
        _assetsLiveData.value = currentList
    }

}