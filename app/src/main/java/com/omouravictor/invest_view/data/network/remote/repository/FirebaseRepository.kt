package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel

interface FirebaseRepository {
    fun saveAsset(assetUiModel: AssetUiModel): Result<AssetUiModel>
}