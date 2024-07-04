package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.di.model.DispatcherProvider
import com.omouravictor.invest_view.presenter.model.UiState
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

    private val _assetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val assetUiState = _assetUiState.asStateFlow()

    private val _assetsList = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val assetsList = _assetsList.asStateFlow()

    private val _assetsListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val assetsListUiState = _assetsListUiState.asStateFlow()

    init {
        loadAssetsList()
    }

    fun loadAssetsList() {
        _assetsListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.getAssetsList() }
                if (result.isSuccess) {
                    val assetsListResult = result.getOrThrow()
                    _assetsList.value = assetsListResult
                    _assetsListUiState.value = UiState.Success(assetsListResult)
                } else
                    _assetsListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetsListUiState.value = UiState.Error(e)
            }
        }
    }

    fun saveAsset(asset: AssetUiModel) {
        _assetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.saveAsset(asset) }
                if (result.isSuccess) {
                    val assetResult = result.getOrThrow()
                    val updatedList = _assetsList.value.toMutableList().apply {
                        val index = indexOfFirst { it.symbol == assetResult.symbol }
                        if (index > -1)
                            set(index, assetResult)
                        else
                            add(assetResult)
                    }
                    _assetUiState.value = UiState.Success(assetResult)
                    _assetsList.value = updatedList
                    _assetsListUiState.value = UiState.Success(updatedList)
                } else
                    _assetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetUiState.value = UiState.Error(e)
            }
        }
    }

    fun deleteAsset(asset: AssetUiModel) {
        _assetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(dispatchers.io) { firebaseRepository.deleteAsset(asset) }
                if (result.isSuccess) {
                    val assetResult = result.getOrThrow()
                    _assetUiState.value = UiState.Success(assetResult)
                    _assetsList.value -= assetResult
                    _assetsListUiState.value = UiState.Success(_assetsList.value)
                } else
                    _assetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetUiState() {
        _assetUiState.value = UiState.Initial
    }

}