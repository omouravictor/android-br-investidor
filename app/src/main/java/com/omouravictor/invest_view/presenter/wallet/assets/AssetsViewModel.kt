package com.omouravictor.invest_view.presenter.wallet.assets

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
class AssetsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteAssetsRepository: RemoteAssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val currentAssets get() = _assetsList.value.orEmpty()
    val currentAssetTypes get() = currentAssets.map { it.assetType }.distinct()
    private val _assetsList = MutableLiveData<List<AssetUiModel>>()
    val assetsList: LiveData<List<AssetUiModel>> = _assetsList

    fun addAsset(assetUiModel: AssetUiModel) {
        val currentList = currentAssets.toMutableList()
        currentList.add(assetUiModel)
        _assetsList.value = currentList
    }

}