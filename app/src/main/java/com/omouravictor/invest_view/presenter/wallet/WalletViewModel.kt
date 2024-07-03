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

    private val _uiStateFlow = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    private val _assetsListStateFlow = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    val assetsListStateFlow = _assetsListStateFlow.asStateFlow()

    init {
        loadAssets()
    }

    fun loadAssets() {
        _uiStateFlow.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.getAssetsList() }
                if (result.isSuccess) {
                    val resultsList = result.getOrThrow()
                    _assetsListStateFlow.value = resultsList
                    _uiStateFlow.value = UiState.Success(resultsList)
                } else
                    _uiStateFlow.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _uiStateFlow.value = UiState.Error(e)
            }
        }
    }

    fun addAsset(asset: AssetUiModel) {
        _assetsListStateFlow.value += asset
        _uiStateFlow.value = UiState.Success(_assetsListStateFlow.value)
    }

    fun removeAsset(asset: AssetUiModel) {
        _assetsListStateFlow.value -= asset
        _uiStateFlow.value = UiState.Success(_assetsListStateFlow.value)
    }

    fun updateAsset(data: AssetUiModel) {
        val assetsList = _assetsListStateFlow.value.toMutableList()
        val index = assetsList.indexOfFirst { it.symbol == data.symbol }
        if (index != -1) {
            assetsList[index] = data
            _assetsListStateFlow.value = assetsList
            _uiStateFlow.value = UiState.Success(_assetsListStateFlow.value)
        }
    }

}