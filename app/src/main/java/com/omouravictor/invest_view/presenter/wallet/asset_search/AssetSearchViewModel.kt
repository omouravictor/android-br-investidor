package com.omouravictor.invest_view.presenter.wallet.asset_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.remote.model.asset_quote.toAssetQuoteUiModel
import com.omouravictor.invest_view.data.remote.model.assets_by_search.toAssetsUiModel
import com.omouravictor.invest_view.data.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetQuoteUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetSearchViewModel @Inject constructor(
    private val assetsApiRepository: AssetsApiRepository
) : ViewModel() {

    private val _assetBySearchListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val assetBySearchListUiState = _assetBySearchListUiState.asStateFlow()

    private val _assetQuoteUiState = MutableStateFlow<UiState<AssetQuoteUiModel>>(UiState.Initial)
    val assetQuoteUiState = _assetQuoteUiState.asStateFlow()

    fun loadAssetsBySearch(keywords: String) {
        _assetBySearchListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetsBySearch(keywords)
                if (result.isSuccess) {
                    val assetsBySearchList = result.getOrThrow().toAssetsUiModel()
                    _assetBySearchListUiState.value = UiState.Success(assetsBySearchList)
                } else
                    _assetBySearchListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetBySearchListUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadAssetQuote(symbol: String) {
        _assetQuoteUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(symbol)
                if (result.isSuccess) {
                    val assetQuoteUiModel = result.getOrThrow().toAssetQuoteUiModel()
                    _assetQuoteUiState.value = UiState.Success(assetQuoteUiModel)
                } else
                    _assetQuoteUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetQuoteUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetAssetQuoteUiState() {
        _assetQuoteUiState.value = UiState.Initial
    }

}