package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.remote.model.asset_quote.toGlobalQuoteUiModel
import com.omouravictor.invest_view.data.remote.repository.AssetsApiRepository
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel
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

    private val _deleteAssetUiState = MutableStateFlow<UiState<AssetUiModel>>(UiState.Initial)
    val deleteAssetUiState = _deleteAssetUiState.asStateFlow()

    private val _getUserAssetListUiState = MutableStateFlow<UiState<List<AssetUiModel>>>(UiState.Initial)
    val getUserAssetListUiState = _getUserAssetListUiState.asStateFlow()

    private val _assetList = MutableStateFlow<List<AssetUiModel>>(emptyList())
    val assetList = _assetList.asStateFlow()

    fun getUserAssetList(userId: String) {
        _getUserAssetListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val assetList = firebaseRepository
                    .getAssetList(userId)
                    .getOrThrow()

                updatePrices(userId, assetList)
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
                val updatedList = getUpdatedList(result)
                _saveAssetUiState.value = UiState.Success(result)
                _assetList.value = updatedList
                _getUserAssetListUiState.value = UiState.Success(updatedList)
            } catch (e: Exception) {
                _saveAssetUiState.value = UiState.Error(e)
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

    fun resetSaveAssetUiState() {
        _saveAssetUiState.value = UiState.Initial
    }

    fun resetDeleteAssetUiState() {
        _deleteAssetUiState.value = UiState.Initial
    }

    private suspend fun updatePrices(userId: String, assetList: List<AssetUiModel>) {
        for (asset in assetList) {
            try {
                val globalQuote = assetsApiRepository
                    .getAssetGlobalQuote(asset.symbol)
                    .getOrThrow()
                    .toGlobalQuoteUiModel()

                asset.price = globalQuote.price
                firebaseRepository.saveAsset(userId, asset)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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