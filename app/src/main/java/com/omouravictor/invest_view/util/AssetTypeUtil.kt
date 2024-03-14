package com.omouravictor.invest_view.util

import android.content.Context
import androidx.core.content.ContextCompat.getColorStateList
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.wallet.assets.asset_types.model.AssetTypeUiModel
import com.omouravictor.invest_view.presenter.wallet.assets.asset_types.model.AssetTypes

object AssetTypeUtil {

    fun getAssetTypeList(context: Context): List<AssetTypeUiModel> = listOf(
        AssetTypeUiModel(
            assetTypes = AssetTypes.EQUITY,
            description = context.getString(R.string.stocks),
            color = getColorStateList(context, R.color.equity)!!
        ),
        AssetTypeUiModel(
            assetTypes = AssetTypes.MUTUAL_FUND,
            description = context.getString(R.string.investment_funds),
            color = getColorStateList(context, R.color.mutual_fund)!!
        )
    )

}