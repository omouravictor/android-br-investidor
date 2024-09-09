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

    private val _saveAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val saveAssetUiState = _saveAssetUiState.asStateFlow()

    private val _deleteAssetUiState = MutableStateFlow<UiState<Boolean>>(UiState.Initial)
    val deleteAssetUiState = _deleteAssetUiState.asStateFlow()

    private val _getAssetListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val getAssetListUiState = _getAssetListUiState.asStateFlow()

    private val _assetList = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val assetList = _assetList.asStateFlow()

    private val userId: String = auth.currentUser?.uid ?: ""

    init {
        loadAssetList()
    }

    fun loadAssetList() {
        _getAssetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.getAssetList(userId)
                if (result.isSuccess) {
                    val assetsListResult = result.getOrThrow()
                    _assetList.value = assetsListResult
                    _getAssetListUiState.value = UiState.Success(assetsListResult)
                } else
                    _getAssetListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getAssetListUiState.value = UiState.Error(e)
            }
        }
    }

    fun saveAsset(asset: AssetUiModel) {
        _saveAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.saveAsset(userId, asset)
                if (result.isSuccess) {
                    val assetResult = result.getOrThrow()
                    val updatedList = getUpdatedList(assetResult)
                    _saveAssetUiState.value = UiState.Success(assetResult)
                    _assetList.value = updatedList
                    _getAssetListUiState.value = UiState.Success(updatedList)
                } else
                    _saveAssetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _saveAssetUiState.value = UiState.Error(e)
            }
        }
    }

    fun deleteAsset(asset: AssetUiModel) {
        _deleteAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.deleteAsset(userId, asset)
                if (result.isSuccess) {
                    val assetResult = result.getOrThrow()
                    _deleteAssetUiState.value = UiState.Success(true)
                    _assetList.value -= assetResult
                    _getAssetListUiState.value = UiState.Success(_assetList.value)
                } else
                    _deleteAssetUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _deleteAssetUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetSaveAssetUiState() {
        _saveAssetUiState.value = UiState.Initial
    }

    fun resetDeleteAssetUiState() {
        _deleteAssetUiState.value = UiState.Initial
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