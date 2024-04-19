package com.omouravictor.invest_view.presenter.wallet.assets

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.toAssetsBySearchUiModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.util.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assets = MutableLiveData<UiState<List<AssetBySearchUiModel>>>()
    val assets: LiveData<UiState<List<AssetBySearchUiModel>>> = _assets

    fun addAsset(keywords: AssetUiModel) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                remoteAssetsRepository.getAssetsBySearch("")
            }.collectLatest {
                when (it) {
                    is NetworkState.Loading -> handleRemoteAssetsBySearchLoading()
                    is NetworkState.Success -> handleRemoteAssetsBySearchSuccess(it.data)
                    is NetworkState.Error -> handleRemoteAssetsBySearchError(it.e)
                }
            }
        }
    }

    private fun handleRemoteAssetsBySearchLoading() {
        _assets.value = UiState.Loading
    }

    private fun handleRemoteAssetsBySearchSuccess(assetsBySearchResponse: AssetsBySearchResponse) {
        val assetsBySearchUiModel = assetsBySearchResponse.toAssetsBySearchUiModel()
        _assets.value = UiState.Success(assetsBySearchUiModel)
    }

    private fun handleRemoteAssetsBySearchError(e: Exception) {
        _assets.value = UiState.Error(AppUtil.getGenericNetworkErrorMessage(context, e))
    }

}