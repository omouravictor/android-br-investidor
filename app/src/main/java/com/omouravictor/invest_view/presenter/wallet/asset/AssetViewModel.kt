package com.omouravictor.invest_view.presenter.wallet.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.remote.model.asset_quote.GlobalQuote
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
class AssetViewModel @Inject constructor(
    private val assetsApiRepository: AssetsApiRepository
) : ViewModel() {

    private val _getAssetsBySearchListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val getAssetsBySearchListUiState = _getAssetsBySearchListUiState.asStateFlow()

    private val _getUpdatedAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val getUpdatedAssetUiState = _getUpdatedAssetUiState.asStateFlow()

    private val _getQuoteUiState = MutableStateFlow<UiState<GlobalQuote>>(UiState.Initial)
    val getQuoteUiState = _getQuoteUiState.asStateFlow()

    fun loadAssetsBySearch(keywords: String) {
        _getAssetsBySearchListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetsBySearch(keywords)
                if (result.isSuccess)
                    _getAssetsBySearchListUiState.value = UiState.Success(result.getOrThrow().toAssetsUiModel())
                else
                    _getAssetsBySearchListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getAssetsBySearchListUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadQuoteFor(assetUiModel: AssetUiModel) {
        _getUpdatedAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(assetUiModel.symbol)
                if (result.isSuccess) {
                    val assetQuote = result.getOrThrow().globalQuote
                    assetUiModel.price = assetQuote.price
                    _getUpdatedAssetUiState.value = UiState.Success(assetUiModel)
                } else
                    _getUpdatedAssetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getUpdatedAssetUiState.value = UiState.Error(e)
            }
        }
    }

    fun loadQuoteFor(symbol: String) {
        _getQuoteUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = assetsApiRepository.getAssetGlobalQuote(symbol)
                if (result.isSuccess)
                    _getQuoteUiState.value = UiState.Success(result.getOrThrow().globalQuote)
                else
                    _getQuoteUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getQuoteUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetGetUpdatedAssetUiState() {
        _getUpdatedAssetUiState.value = UiState.Initial
    }

}