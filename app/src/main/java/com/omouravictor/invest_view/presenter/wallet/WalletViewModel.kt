package com.omouravictor.invest_view.presenter.wallet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
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
    val assetCurrencyPairsList get() = assetsList.map { Pair(it.currency, it.currencyResColor) }.distinct()
    val assetRegionsList get() = assetsList.map { it.region }.distinct()

    init {
        val testList = listOf(
            AssetUiModel(
                symbol = "AAPL",
                name = "Apple Inc.",
                assetType = AssetTypes.STOCK,
                region = "United States",
                currency = "USD",
                currencyResColor = R.color.usd,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "AAPL34",
                name = "Apple Inc.",
                assetType = AssetTypes.BDR,
                region = "Brazil/Sao Paolo",
                currency = "BRL",
                currencyResColor = R.color.brl,
                price = 50.0,
                amount = 10,
                totalInvested = 1000.0
            ),
            AssetUiModel(
                symbol = "MSFT",
                name = "Apple Inc.",
                assetType = AssetTypes.ETF,
                region = "Amsterdam",
                currency = "EUR",
                currencyResColor = R.color.eur,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "VALE3",
                name = "Vale S.A.",
                assetType = AssetTypes.BRAZILIAN_STOCK,
                region = "Brazil/Sao Paolo",
                currency = "CNY",
                currencyResColor = R.color.cny,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "MXRF11",
                name = "Max renda.",
                assetType = AssetTypes.FI,
                region = "Brazil/Sao Paolo",
                currency = "INR",
                currencyResColor = R.color.inr,
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "ASDAS",
                name = "Vale S.A.",
                assetType = AssetTypes.OTHER,
                region = "Brazil/Sao Paolo",
                currency = "CAD",
                currencyResColor = R.color.cad,
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