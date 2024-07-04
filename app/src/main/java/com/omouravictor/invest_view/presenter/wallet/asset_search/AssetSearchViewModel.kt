package com.omouravictor.invest_view.presenter.wallet.asset_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.toAssetQuoteUiModel
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.toAssetsUiModel
import com.omouravictor.invest_view.data.network.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.di.model.DispatcherProvider
import com.omouravictor.invest_view.presenter.wallet.model.AssetQuoteUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.UiState
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

    private val _assetsBySearchUiStateFlow = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    private val _assetQuoteUiStateFlow = MutableStateFlow<UiState<AssetQuoteUiModel>>(UiState.Initial)
    val assetsBySearchUiStateFlow = _assetsBySearchUiStateFlow.asStateFlow()
    val assetQuoteUiStateFlow = _assetQuoteUiStateFlow.asStateFlow()

    fun loadAssetsBySearch(keywords: String) {
        _assetsBySearchUiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { assetsApiRepository.getAssetsBySearch(keywords) }
                if (result.isSuccess) {
                    val assetsBySearchList = result.getOrThrow().toAssetsUiModel()
                    _assetsBySearchUiStateFlow.value = UiState.Success(assetsBySearchList)
                } else
                    _assetsBySearchUiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetsBySearchUiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun loadAssetQuote(symbol: String) {
        _assetQuoteUiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { assetsApiRepository.getAssetGlobalQuote(symbol) }
                if (result.isSuccess) {
                    val assetQuoteUiModel = result.getOrThrow().toAssetQuoteUiModel()
                    _assetQuoteUiStateFlow.value = UiState.Success(assetQuoteUiModel)
                } else
                    _assetQuoteUiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetQuoteUiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun resetAssetQuoteUiState() {
        _assetQuoteUiStateFlow.value = UiState.Initial
    }

}