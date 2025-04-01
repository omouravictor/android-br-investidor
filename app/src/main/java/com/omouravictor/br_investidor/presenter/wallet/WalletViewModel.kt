package com.omouravictor.br_investidor.presenter.wallet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.repository.AssetsApiRepository
import com.omouravictor.br_investidor.data.remote.firebase.FirebaseRepository
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel
import com.omouravictor.br_investidor.util.getRoundedDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val assetsApiRepository: AssetsApiRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _saveAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val saveAssetUiState = _saveAssetUiState.asStateFlow()

    private val _updateAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val updateAssetUiState = _updateAssetUiState.asStateFlow()

    private val _deleteAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val deleteAssetUiState = _deleteAssetUiState.asStateFlow()

    private val _getUserAssetListUiState =
        MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val getUserAssetListUiState = _getUserAssetListUiState.asStateFlow()

    private val _assetList = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val assetList = _assetList.asStateFlow()

    fun getUserAssetList(userId: String) {
        _getUserAssetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val assetList = firebaseRepository.getAssetList(userId).getOrThrow()

                for (asset in assetList) {
                    val globalQuote = assetsApiRepository
                        .getAssetGlobalQuote(asset.symbol)
                        .getOrNull()?.globalQuoteResponse ?: break

                    asset.price = globalQuote.price.getRoundedDouble()
                }

                _assetList.value = assetList
                _getUserAssetListUiState.value = UiState.Success(assetList)

            } catch (e: Exception) {
                _getUserAssetListUiState.value = UiState.Error(e)
            }
        }
    }

    fun saveAsset(asset: AssetUiModel, userId: String) {
        _saveAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.saveAsset(userId, asset).getOrThrow()
                val updatedList = _assetList.value + result
                _saveAssetUiState.value = UiState.Success(result)
                _assetList.value = updatedList
                _getUserAssetListUiState.value = UiState.Success(updatedList)

            } catch (e: Exception) {
                _saveAssetUiState.value = UiState.Error(e)
            }
        }
    }

    fun updateAsset(asset: AssetUiModel, userId: String) {
        _updateAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.saveAsset(userId, asset).getOrThrow()
                _updateAssetUiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _updateAssetUiState.value = UiState.Error(e)
            }
        }
    }

    /* This function is used to update the asset price in the database in case the
     limit of API requests is reached in the function getUserAssetList. */
    fun updateAssetInDataBase(asset: AssetUiModel, userId: String) {
        viewModelScope.launch {
            try {
                firebaseRepository.saveAsset(userId, asset).getOrThrow()
            } catch (e: Exception) {
                Log.e("UpdateAsset", "UserId: $userId | Asset: ${asset.symbol}", e)
            }
        }
    }

    fun deleteAsset(asset: AssetUiModel, userId: String) {
        _deleteAssetUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = firebaseRepository.deleteAsset(userId, asset).getOrThrow()
                _deleteAssetUiState.value = UiState.Success(result)
                _assetList.value -= result
                _getUserAssetListUiState.value = UiState.Success(_assetList.value)

            } catch (e: Exception) {
                _deleteAssetUiState.value = UiState.Error(e)
            }
        }
    }

    fun deleteAllUserAssets(userId: String) {
        _getUserAssetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                firebaseRepository.deleteAllUserAssets(userId, assetList.value).getOrThrow()
                _assetList.value = emptyList()
                _getUserAssetListUiState.value = UiState.Success(_assetList.value)

            } catch (e: Exception) {
                _getUserAssetListUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetSaveAssetUiState() {
        _saveAssetUiState.value = UiState.Initial
    }

    fun resetUpdateAssetUiState() {
        _updateAssetUiState.value = UiState.Initial
    }

    fun resetDeleteAssetUiState() {
        _deleteAssetUiState.value = UiState.Initial
    }

}