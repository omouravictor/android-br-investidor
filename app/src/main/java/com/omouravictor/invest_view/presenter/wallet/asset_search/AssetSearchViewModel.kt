package com.omouravictor.invest_view.presenter.wallet.asset_search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.toAssetQuoteUiModel
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.toAssetsBySearchUiModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetQuoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetSearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsBySearch = MutableLiveData<UiState<List<AssetBySearchUiModel>>>()
    private val _assetQuote = MutableLiveData<UiState<AssetQuoteUiModel>>()
    val assetsBySearch: LiveData<UiState<List<AssetBySearchUiModel>>> = _assetsBySearch
    val assetQuote: LiveData<UiState<AssetQuoteUiModel>> = _assetQuote

    fun getAssetsBySearch(keywords: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                remoteAssetsRepository.getAssetsBySearch(keywords)
            }.collectLatest {
                when (it) {
                    is NetworkState.Loading -> handleRemoteDataLoading()
                    is NetworkState.Success -> handleRemoteAssetsBySearchSuccess(it.data)
                    is NetworkState.Error -> handleRemoteDataError(it.e)
                }
            }
        }
    }

    fun getAssetQuote(symbol: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                remoteAssetsRepository.getAssetGlobalQuote(symbol)
            }.collectLatest {
                when (it) {
                    is NetworkState.Loading -> handleRemoteDataLoading()
                    is NetworkState.Success -> handleRemoteAssetQuoteSuccess(it.data)
                    is NetworkState.Error -> handleRemoteDataError(it.e)
                }
            }
        }
    }

    fun clearLiveDataValues() {
        _assetsBySearch.value = UiState.Empty
        _assetQuote.value = UiState.Empty
    }

    private fun handleRemoteDataLoading() {
        _assetsBySearch.value = UiState.Loading
        _assetQuote.value = UiState.Loading
    }

    private fun handleRemoteAssetsBySearchSuccess(assetsBySearchResponse: AssetsBySearchResponse) {
        val assetsBySearchUiModel = assetsBySearchResponse.toAssetsBySearchUiModel()
        _assetsBySearch.value = UiState.Success(assetsBySearchUiModel)
    }

    private fun handleRemoteAssetQuoteSuccess(assetGlobalQuoteResponse: AssetGlobalQuoteResponse) {
        val assetQuoteUiModel = assetGlobalQuoteResponse.toAssetQuoteUiModel()
        _assetQuote.value = UiState.Success(assetQuoteUiModel)
    }

    private fun handleRemoteDataError(e: Exception) {
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