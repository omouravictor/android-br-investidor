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

    private val _assetsList = MutableLiveData<List<AssetUiModel>>()
    val assetsList: LiveData<List<AssetUiModel>> = _assetsList

    init {
        val testList = listOf(
            AssetUiModel(
                symbol = "AAPL",
                name = "Apple Inc.",
                assetType = AssetTypes.EQUITY,
                region = "United States",
                currency = "USD",
                price = 150.0,
                amount = 10,
                totalInvested = 1600.0
            ),
            AssetUiModel(
                symbol = "MXRF11",
                name = "Max Retail Fund",
                assetType = AssetTypes.MUTUAL_FUND,
                region = "Brazil",
                currency = "BRL",
                price = 10.0,
                amount = 100,
                totalInvested = 900.0
            )
        )
        _assetsList.value = testList
    }

    fun addAsset(assetUiModel: AssetUiModel) {
        val currentList = _assetsList.value.orEmpty().toMutableList()
        currentList.add(0, assetUiModel)
        _assetsList.value = currentList
    }

}