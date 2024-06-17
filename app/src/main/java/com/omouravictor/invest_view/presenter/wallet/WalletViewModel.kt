package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _walletUiStateFlow = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    private val _assetsListStateFlow = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val walletUiStateFlow = _walletUiStateFlow.asStateFlow()
    val assetsListStateFlow = _assetsListStateFlow.asStateFlow()
    // TODO: testar depois a carteira vazia no banco pra ver se o empty layout aparece

    fun getAssetsList() {
        _walletUiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.getAssetsList() }
                if (result.isSuccess) {
                    val assetsList = result.getOrThrow()
                    _assetsListStateFlow.value = assetsList
                    _walletUiStateFlow.value = UiState.Success(Unit)
                } else {
                    _walletUiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
                }
            } catch (e: Exception) {
                _walletUiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun saveAsset(asset: AssetUiModel) {
        _walletUiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.saveAsset(asset) }
                if (result.isSuccess) {
                    _assetsListStateFlow.value += result.getOrThrow()
                    _walletUiStateFlow.value = UiState.Success(Unit)
                } else {
                    _walletUiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
                }
            } catch (e: Exception) {
                _walletUiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun resetWalletUiStateFlow() {
        _walletUiStateFlow.value = UiState.Empty
    }

}