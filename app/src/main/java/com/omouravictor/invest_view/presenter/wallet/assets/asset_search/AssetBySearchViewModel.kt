package com.omouravictor.invest_view.presenter.wallet.assets.asset_search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.NetworkResultState
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.toAssetsBySearchUiModel
import com.omouravictor.invest_view.data.network.remote.repository.AssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiResultState
import com.omouravictor.invest_view.presenter.wallet.assets.asset_search.model.AssetBySearchUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetBySearchViewModel @Inject constructor(
    private val assetsRepository: AssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsBySearch = MutableLiveData<UiResultState<List<AssetBySearchUiModel>>>()
    val assetsBySearch: LiveData<UiResultState<List<AssetBySearchUiModel>>> = _assetsBySearch

    fun getAssetsBySearch(keywords: String) {
        viewModelScope.launch(dispatchers.io) {
            assetsRepository.getRemoteAssetsBySearch(keywords).collectLatest { result ->
                when (result) {
                    is NetworkResultState.Success -> handleNetworkSuccessResult(result.data)
                    is NetworkResultState.Error -> handleNetworkErrorResult(result.e)
                    is NetworkResultState.Loading -> handleNetworkLoadingResult()
                }
            }
        }
    }

    private fun handleNetworkSuccessResult(assetsBySearchResponse: AssetsBySearchResponse) {
        _assetsBySearch.postValue(UiResultState.Success(assetsBySearchResponse.toAssetsBySearchUiModel()))
    }

    private fun handleNetworkErrorResult(e: Exception) {
        println("Error: $e")
    }

    private fun handleNetworkLoadingResult() {
    }

}