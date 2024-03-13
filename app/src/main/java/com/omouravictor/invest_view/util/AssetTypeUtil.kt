package com.omouravictor.invest_view.util

import android.content.Context
import androidx.core.content.ContextCompat.getColorStateList
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.ui.wallet.assets.asset_type.model.AssetType
import com.omouravictor.invest_view.ui.wallet.assets.asset_type.model.AssetTypeUi

object AssetTypeUtil {

    fun getAssetTypeList(context: Context): List<AssetTypeUi> = listOf(
        AssetTypeUi(
            assetType = AssetType.EQUITY,
            description = context.getString(R.string.stocks),
            color = getColorStateList(context, R.color.equity)!!
        ),
        AssetTypeUi(
            assetType = AssetType.MUTUAL_FUND,
            description = context.getString(R.string.investment_funds),
            color = getColorStateList(context, R.color.mutual_fund)!!
        )
    )

}