package com.omouravictor.invest_view.presenter.wallet.asset_search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.toAssetQuoteUiModel
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.toAssetsBySearchUiModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetQuoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetSearchViewModel @Inject constructor(
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsBySearch = MutableLiveData<UiState<List<AssetBySearchUiModel>>>()
    private val _assetQuote = MutableLiveData<UiState<AssetQuoteUiModel>>()
    val assetsBySearch: LiveData<UiState<List<AssetBySearchUiModel>>> = _assetsBySearch
    val assetQuote: LiveData<UiState<AssetQuoteUiModel>> = _assetQuote

    fun getAssetsBySearch(keywords: String) {
        viewModelScope.launch {
            _assetsBySearch.value = UiState.Loading

            withContext(dispatchers.io) {
                remoteAssetsRepository.getAssetsBySearch(keywords)
            }.collectLatest {
                when (it) {
                    is NetworkState.Success -> {
                        val assetsBySearchUiModel = it.data.toAssetsBySearchUiModel()
                        _assetsBySearch.value = UiState.Success(assetsBySearchUiModel)
                    }

                    is NetworkState.Error -> _assetsBySearch.value = UiState.Error(it.e)
                    is NetworkState.Loading -> _assetsBySearch.value = UiState.Loading
                }
            }
        }
    }

    fun getAssetQuote(symbol: String) {
        viewModelScope.launch {
            _assetQuote.value = UiState.Loading

            withContext(dispatchers.io) {
                remoteAssetsRepository.getAssetGlobalQuote(symbol)
            }.collectLatest {
                when (it) {
                    is NetworkState.Success -> {
                        val assetQuoteUiModel = it.data.toAssetQuoteUiModel()
                        _assetQuote.value = UiState.Success(assetQuoteUiModel)
                    }

                    is NetworkState.Error -> _assetQuote.value = UiState.Error(it.e)
                    is NetworkState.Loading -> _assetQuote.value = UiState.Loading
                }
            }
        }
    }

    fun clearAssetQuoteLiveData() {
        _assetQuote.value = UiState.Empty
    }

}