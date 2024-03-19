package com.omouravictor.invest_view.presenter.wallet.asset_search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.model.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.toAssetsBySearchUiModel
import com.omouravictor.invest_view.data.network.remote.repository.AssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.model.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetBySearchViewModel @Inject constructor(
    private val assetsRepository: AssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsBySearch = MutableLiveData<UiState<List<AssetBySearchUiModel>>>()
    val assetsBySearch: LiveData<UiState<List<AssetBySearchUiModel>>> = _assetsBySearch

    fun getAssetsBySearch(keywords: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                assetsRepository.getRemoteAssetsBySearch(keywords)
            }.collectLatest { networkState ->
                when (networkState) {
                    is NetworkState.Loading -> handleRemoteAssetsBySearchLoading()
                    is NetworkState.Success -> handleRemoteAssetsBySearchSuccess(networkState.data)
                    is NetworkState.Error -> handleRemoteAssetsBySearchError(networkState.e)
                }
            }
        }
    }

    fun clearAssetsBySearch() {
        _assetsBySearch.value = UiState.Empty
    }

    private fun handleRemoteAssetsBySearchLoading() {
        _assetsBySearch.value = UiState.Loading
    }

    private fun handleRemoteAssetsBySearchSuccess(assetsBySearchResponse: AssetsBySearchResponse) {
        val assetsBySearchUiModel = assetsBySearchResponse.toAssetsBySearchUiModel()
        _assetsBySearch.value = UiState.Success(assetsBySearchUiModel)
    }

    private fun handleRemoteAssetsBySearchError(e: Exception) {
    }

}