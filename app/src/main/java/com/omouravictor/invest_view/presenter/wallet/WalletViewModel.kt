package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _assetInOperationUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val assetInOperationUiState = _assetInOperationUiState.asStateFlow()

    private val _assetList = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val assetList = _assetList.asStateFlow()

    private val _assetListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val assetListUiState = _assetListUiState.asStateFlow()

    private val userId: String = auth.currentUser?.uid ?: ""

    init {
        loadAssetList()
    }

    fun loadAssetList() {
        _assetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.getAssetList(userId)
                if (result.isSuccess) {
                    val assetsListResult = result.getOrThrow()
                    _assetList.value = assetsListResult
                    _assetListUiState.value = UiState.Success(assetsListResult)
                } else
                    _assetListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetListUiState.value = UiState.Error(e)
            }
        }
    }

    fun saveAsset(asset: AssetUiModel) {
        _assetInOperationUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.saveAsset(userId, asset)
                if (result.isSuccess) {
                    val assetResult = result.getOrThrow()
                    val updatedList = getUpdatedList(assetResult)
                    _assetInOperationUiState.value = UiState.Success(assetResult)
                    _assetList.value = updatedList
                    _assetListUiState.value = UiState.Success(updatedList)
                } else
                    _assetInOperationUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetInOperationUiState.value = UiState.Error(e)
            }
        }
    }

    fun deleteAsset(asset: AssetUiModel) {
        _assetInOperationUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.deleteAsset(userId, asset)
                if (result.isSuccess) {
                    val assetResult = result.getOrThrow()
                    _assetInOperationUiState.value = UiState.Success(assetResult)
                    _assetList.value -= assetResult
                    _assetListUiState.value = UiState.Success(_assetList.value)
                } else
                    _assetInOperationUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _assetInOperationUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetAssetInOperationUiState() {
        _assetInOperationUiState.value = UiState.Initial
    }

    private fun getUpdatedList(asset: AssetUiModel): List<AssetUiModel> {
        return _assetList.value.toMutableList().apply {
            val index = indexOfFirst { it.symbol == asset.symbol }
            if (index > -1)
                set(index, asset)
            else
                add(asset)
        }
    }

}