package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.toAssetsBySearchUiModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsBySearchRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetSearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsBySearchRepository: RemoteAssetsBySearchRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsBySearch = MutableLiveData<UiState<List<AssetBySearchUiModel>>>()
    val assetsBySearch: LiveData<UiState<List<AssetBySearchUiModel>>> = _assetsBySearch

    fun getAssetsBySearch(keywords: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                remoteAssetsBySearchRepository.getAssetsBySearch(keywords)
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
        when (e) {
            is java.net.UnknownHostException -> _assetsBySearch.value =
                UiState.Error(context.getString(R.string.noInternetConnection))

            is java.net.SocketTimeoutException -> _assetsBySearch.value =
                UiState.Error(context.getString(R.string.checkInternetConnection))

            else -> _assetsBySearch.value =
                UiState.Error(context.getString(R.string.somethingWentWrong))
        }
    }

}