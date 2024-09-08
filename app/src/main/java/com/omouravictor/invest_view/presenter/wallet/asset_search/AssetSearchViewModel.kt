package com.omouravictor.invest_view.presenter.wallet.asset_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.remote.model.asset_quote.AssetGlobalQuoteItemResponse
import com.omouravictor.invest_view.data.remote.model.assets_by_search.toAssetsUiModel
import com.omouravictor.invest_view.data.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.presenter.model.UiState
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

    private val _assetListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val assetListUiState = _assetListUiState.asStateFlow()

    private val _assetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val assetUiState = _assetUiState.asStateFlow()

    private val _quoteUiState = MutableStateFlow<UiState<AssetGlobalQuoteItemResponse>>(UiState.Initial)
    val quoteUiState = _quoteUiState.asStateFlow()

    fun loadAssetsBySearch(keywords: String) {
        _assetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetsBySearch(keywords)
                if (result.isSuccess) {
                    val assetsBySearchList = result.getOrThrow().toAssetsUiModel()
                    _assetListUiState.value = UiState.Success(assetsBySearchList)
                } else
                    _assetListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetListUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadAssetQuote(assetUiModel: AssetUiModel) {
        _assetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(assetUiModel.symbol)
                if (result.isSuccess) {
                    val assetQuote = result.getOrThrow().globalQuote
                    assetUiModel.price = assetQuote.price
                    _assetUiState.value = UiState.Success(assetUiModel)
                } else
                    _assetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadAssetQuote(symbol: String) {
        _quoteUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(symbol)
                if (result.isSuccess) {
                    val assetQuote = result.getOrThrow().globalQuote
                    _quoteUiState.value = UiState.Success(assetQuote)
                } else
                    _quoteUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _quoteUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetAssetQuoteUiState() {
        _quoteUiState.value = UiState.Initial
    }

}