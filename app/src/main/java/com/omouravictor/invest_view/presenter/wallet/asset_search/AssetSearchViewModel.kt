package com.omouravictor.invest_view.presenter.wallet.asset_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.toAssetQuoteUiModel
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.toAssetsUiModel
import com.omouravictor.invest_view.data.network.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetQuoteUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetSearchViewModel @Inject constructor(
    private val assetsApiRepository: AssetsApiRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsBySearchListStateFlow = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    private val _assetQuoteStateFlow = MutableStateFlow<UiState<AssetQuoteUiModel>>(UiState.Initial)
    val assetsBySearchListStateFlow = _assetsBySearchListStateFlow.asStateFlow()
    val assetQuoteStateFlow = _assetQuoteStateFlow.asStateFlow()

    fun getAssetsBySearch(keywords: String) {
        _assetsBySearchListStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { assetsApiRepository.getAssetsBySearch(keywords) }
                if (result.isSuccess) {
                    val assetsBySearchList = result.getOrThrow().toAssetsUiModel()
                    _assetsBySearchListStateFlow.value = UiState.Success(assetsBySearchList)
                } else
                    _assetsBySearchListStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetsBySearchListStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun getAssetQuote(symbol: String) {
        _assetQuoteStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { assetsApiRepository.getAssetGlobalQuote(symbol) }
                if (result.isSuccess) {
                    val assetQuoteUiModel = result.getOrThrow().toAssetQuoteUiModel()
                    _assetQuoteStateFlow.value = UiState.Success(assetQuoteUiModel)
                } else
                    _assetQuoteStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetQuoteStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun resetAssetQuoteLiveData() {
        _assetQuoteStateFlow.value = UiState.Initial
    }

}