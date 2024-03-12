package com.omouravictor.invest_view.util

import android.content.Context
import androidx.core.content.ContextCompat.getColorStateList
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.wallet.asset_type.AssetTypeUiModel
import com.omouravictor.invest_view.presenter.wallet.asset_type.AssetTypes

object AssetTypeUtil {

    fun getAssetTypeList(context: Context): List<AssetTypeUiModel> = listOf(
        AssetTypeUiModel(
            assetType = AssetTypes.STOCKS,
            description = context.getString(R.string.stocks),
            color = getColorStateList(context, R.color.stocks)!!
        ),
        AssetTypeUiModel(
            assetType = AssetTypes.REAL_ESTATE_FUNDS,
            description = context.getString(R.string.real_estate_funds),
            color = getColorStateList(context, R.color.real_estate_funds)!!
        )
    )

}