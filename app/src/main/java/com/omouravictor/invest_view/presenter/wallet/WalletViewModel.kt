package com.omouravictor.invest_view.presenter.wallet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.data.network.remote.repository.RemoteAssetsRepository
import com.omouravictor.invest_view.di.base.DispatcherProvider
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _assetsLiveData = MutableLiveData<List<AssetUiModel>>()
    val assetsLiveData: LiveData<List<AssetUiModel>> = _assetsLiveData
    val assetsList get() = assetsLiveData.value.orEmpty()
    val assetTypesList get() = assetsList.map { it.assetType }.distinct()

    fun addAsset(assetUiModel: AssetUiModel) {
        val currentList = assetsList.toMutableList()
        currentList.add(assetUiModel)
        _assetsLiveData.value = currentList
    }

}