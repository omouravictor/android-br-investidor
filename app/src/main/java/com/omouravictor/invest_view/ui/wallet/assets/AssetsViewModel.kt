package com.omouravictor.invest_view.ui.wallet.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.alpha_vantage.model.asset_search.AssetMatchesResponse
import com.omouravictor.invest_view.data.network.base.NetworkResultState
import com.omouravictor.invest_view.data.repository.AssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val assetsRepository: AssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is assets Fragment"
    }
    val text: LiveData<String> = _text

    fun getAssets(text: String) {
        viewModelScope.launch(dispatchers.io) {
            assetsRepository.getRemoteAssetsBySearch(text).collectLatest { result ->
                when (result) {
                    is NetworkResultState.Success -> handleNetworkSuccessResult(result.data)
                    is NetworkResultState.Error -> handleNetworkErrorResult(result.message)
                    is NetworkResultState.Loading -> handleNetworkLoadingResult()
                }
            }
        }
    }

    private fun handleNetworkSuccessResult(data: AssetMatchesResponse) {
    }

    private fun handleNetworkErrorResult(message: String) {
    }

    private fun handleNetworkLoadingResult() {
    }

}