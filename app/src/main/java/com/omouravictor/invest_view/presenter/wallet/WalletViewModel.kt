package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _walletUiStateFlow = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Empty)
    private val _assetsListStateFlow = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val walletUiStateFlow = _walletUiStateFlow.asStateFlow()
    val assetsListStateFlow = _assetsListStateFlow.asStateFlow()
    val assetsList get() = assetsListStateFlow.value
    val assetTypesList get() = assetsList.map { it.assetType }.distinct()
    val assetCurrenciesList get() = assetsList.map { it.currency }.distinct()

    fun saveAsset(asset: AssetUiModel) {
        viewModelScope.launch {
            _walletUiStateFlow.value = UiState.Loading

            withContext(dispatchers.io) {
                firebaseRepository.saveAsset(asset)
            }.collectLatest { networkState ->
                when (networkState) {
                    is NetworkState.Success -> {
                        val newList = assetsList + asset
                        _assetsListStateFlow.value = newList
                        _walletUiStateFlow.value = UiState.Success(newList)
                    }

                    is NetworkState.Error -> _walletUiStateFlow.value = UiState.Error(networkState.e)
                    is NetworkState.Loading -> _walletUiStateFlow.value = UiState.Loading
                }
            }
        }
    }

    fun resetWalletUiStateFlow() {
        _walletUiStateFlow.value = UiState.Empty
    }

}