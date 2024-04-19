package com.omouravictor.invest_view.presenter.wallet.save_asset

import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.StringUtil

class SaveAssetViewModel : ViewModel() {

    fun getTotalAssetPrice(price: Double, quantity: String): Double {
        return price * StringUtil.getOnlyNumbers(quantity).toLong()
    }

    fun getTotalInvested(totalInvested: String): Double {
        return if (totalInvested.isNotEmpty()) {
            StringUtil.getOnlyNumbers(totalInvested).toDouble() / 100
        } else {
            0.0
        }
    }

    fun getAssetVariation(totalAssetPrice: Double, totalInvested: Double): Double {
        return AssetUtil.getVariation(totalAssetPrice, totalInvested)
    }

}