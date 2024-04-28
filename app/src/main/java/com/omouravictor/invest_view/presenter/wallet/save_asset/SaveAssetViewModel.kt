package com.omouravictor.invest_view.presenter.wallet.save_asset

import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.util.StringUtil

class SaveAssetViewModel : ViewModel() {

    fun getTotalAssetPrice(price: Float, quantity: String): Float {
        return price * StringUtil.getOnlyNumbers(quantity).toLong()
    }

    fun getTotalInvested(totalInvested: String): Float {
        return if (totalInvested.isNotEmpty()) {
            StringUtil.getOnlyNumbers(totalInvested).toFloat() / 100
        } else {
            0f
        }
    }

}