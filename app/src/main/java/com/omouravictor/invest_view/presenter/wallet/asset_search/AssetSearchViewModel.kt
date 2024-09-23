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

    private val _getAssetListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val getAssetListUiState = _getAssetListUiState.asStateFlow()

    private val _getAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val getAssetUiState = _getAssetUiState.asStateFlow()

    private val _getQuoteUiState = MutableStateFlow<UiState<AssetGlobalQuoteItemResponse>>(UiState.Initial)
    val getQuoteUiState = _getQuoteUiState.asStateFlow()

    fun loadAssetsBySearch(keywords: String) {
        _getAssetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetsBySearch(keywords)
                if (result.isSuccess) {
                    val assetsBySearchList = result.getOrThrow().toAssetsUiModel()
                    _getAssetListUiState.value = UiState.Success(assetsBySearchList)
                } else
                    _getAssetListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getAssetListUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadQuoteFor(assetUiModel: AssetUiModel) {
        _getAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(assetUiModel.symbol)
                if (result.isSuccess) {
                    val assetQuote = result.getOrThrow().globalQuote
                    assetUiModel.price = assetQuote.price
                    _getAssetUiState.value = UiState.Success(assetUiModel)
                } else
                    _getAssetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getAssetUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadQuoteFor(symbol: String) {
        _getQuoteUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(symbol)
                if (result.isSuccess) {
                    val assetQuote = result.getOrThrow().globalQuote
                    _getQuoteUiState.value = UiState.Success(assetQuote)
                } else
                    _getQuoteUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getQuoteUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetGetAssetUiState() {
        _getAssetUiState.value = UiState.Initial
    }

}