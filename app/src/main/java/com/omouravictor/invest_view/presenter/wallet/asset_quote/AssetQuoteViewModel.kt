package com.omouravictor.invest_view.presenter.wallet.asset_quote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.toAssetQuoteUiModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetQuoteRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_quote.model.AssetQuoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetQuoteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetQuoteRepository: RemoteAssetQuoteRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetQuote = MutableLiveData<UiState<AssetQuoteUiModel>>()
    val assetQuote: LiveData<UiState<AssetQuoteUiModel>> = _assetQuote

    fun getAssetQuote(symbol: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                remoteAssetQuoteRepository.getAssetGlobalQuoteResponse(symbol)
            }.collectLatest { networkState ->
                when (networkState) {
                    is NetworkState.Loading -> handleRemoteAssetQuoteLoading()
                    is NetworkState.Success -> handleRemoteAssetQuoteSuccess(networkState.data)
                    is NetworkState.Error -> handleRemoteAssetQuoteError(networkState.e)
                }
            }
        }
    }

    fun clearAssetQuote() {
        _assetQuote.value = UiState.Empty
    }

    private fun handleRemoteAssetQuoteLoading() {
        _assetQuote.value = UiState.Loading
    }

    private fun handleRemoteAssetQuoteSuccess(assetGlobalQuoteResponse: AssetGlobalQuoteResponse) {
        val assetQuoteUiModel = assetGlobalQuoteResponse.toAssetQuoteUiModel()
        _assetQuote.value = UiState.Success(assetQuoteUiModel)
    }

    private fun handleRemoteAssetQuoteError(e: Exception) {
        when (e) {
            is java.net.UnknownHostException -> _assetQuote.value =
                UiState.Error(context.getString(R.string.noInternetConnection))

            is java.net.SocketTimeoutException -> _assetQuote.value =
                UiState.Error(context.getString(R.string.checkInternetConnection))

            else -> _assetQuote.value =
                UiState.Error(context.getString(R.string.somethingWentWrong))
        }
    }

}