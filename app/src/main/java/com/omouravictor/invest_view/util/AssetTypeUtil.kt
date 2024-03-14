package com.omouravictor.invest_view.util

import android.content.Context
import com.omouravictor.invest_view.presenter.wallet.assets.asset_types.model.AssetTypeUiModel
import com.omouravictor.invest_view.presenter.wallet.assets.asset_types.model.AssetTypes

object AssetTypeUtil {

    fun getAssetTypeList(context: Context): List<AssetTypeUiModel> =
        AssetTypes.values().map { assetType ->
            AssetTypeUiModel(
                assetTypes = assetType,
                description = context.getString(assetType.description),
                color = context.getColorStateList(assetType.color)
            )
        }
}