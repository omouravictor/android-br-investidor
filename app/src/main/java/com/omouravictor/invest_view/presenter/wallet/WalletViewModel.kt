package com.omouravictor.invest_view.presenter.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.base.UiState
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

    private val _walletUiState = MutableLiveData<UiState<Unit>>()
    private val _assetsListLiveData = MutableLiveData<List<AssetUiModel>>()
    val walletUiState: LiveData<UiState<Unit>> = _walletUiState
    val assetsListLiveData: LiveData<List<AssetUiModel>> = _assetsListLiveData
    val assetsList get() = assetsListLiveData.value.orEmpty()
    val assetTypesList get() = assetsList.map { it.assetType }.distinct()
    val assetCurrenciesList get() = assetsList.map { it.currency }.distinct()

    fun saveAsset(asset: AssetUiModel) {
        viewModelScope.launch {
            _walletUiState.value = UiState.Loading

            withContext(dispatchers.io) {
                firebaseRepository.saveAsset(asset)
            }.collectLatest {
                when (it) {
                    is NetworkState.Success -> {
                        val currentList = assetsList.toMutableList()
                        currentList.add(it.data)
                        _assetsListLiveData.value = currentList
                        _walletUiState.value = UiState.Success(Unit)
                    }

                    is NetworkState.Error -> _walletUiState.value = UiState.Error(it.e)
                    is NetworkState.Loading -> _walletUiState.value = UiState.Loading
                }
            }
        }
    }

}