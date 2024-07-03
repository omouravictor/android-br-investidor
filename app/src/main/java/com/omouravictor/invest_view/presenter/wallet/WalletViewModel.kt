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

    private val _uiStateFlow = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun saveAsset(asset: AssetUiModel) {
        _uiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.saveAsset(asset) }
                if (result.isSuccess)
                    _uiStateFlow.value = UiState.Success(result.getOrThrow())
                else
                    _uiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _uiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun deleteAsset(asset: AssetUiModel) {
        _uiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.deleteAsset(asset) }
                if (result.isSuccess)
                    _uiStateFlow.value = UiState.Success(result.getOrThrow())
                else
                    _uiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _uiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun resetUiState() {
        _uiStateFlow.value = UiState.Initial
    }

}