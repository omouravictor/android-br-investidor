package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _walletUiStateLiveData = MutableLiveData<UiState<Unit>>()
    private val _assetsListLiveData = MutableLiveData<List<AssetUiModel>>()
    val walletUiStateLiveData: LiveData<UiState<Unit>> = _walletUiStateLiveData
    val assetsListLiveData: LiveData<List<AssetUiModel>> = _assetsListLiveData
    val assetsList: List<AssetUiModel> get() = assetsListLiveData.value.orEmpty()
    val assetTypesList: List<AssetTypes> get() = assetsList.map { it.assetType }.distinct()
    val assetCurrenciesList: List<String> get() = assetsList.map { it.currency }.distinct()

    fun saveAsset(asset: AssetUiModel) {
        viewModelScope.launch {
            _walletUiStateLiveData.value = UiState.Loading

            withContext(dispatchers.io) {
                firebaseRepository.saveAsset(asset)
            }.collectLatest {
                when (it) {
                    is NetworkState.Success -> {
                        _assetsListLiveData.value = assetsList + asset
                        _walletUiStateLiveData.value = UiState.Success(Unit)
                    }

                    is NetworkState.Error -> _walletUiStateLiveData.value = UiState.Error(it.e)
                    is NetworkState.Loading -> _walletUiStateLiveData.value = UiState.Loading
                }
            }
        }
    }

    fun clearWalletUiStateLiveData() {
        _walletUiStateLiveData.value = UiState.Empty
    }

}