package com.omouravictor.invest_view.util

import androidx.core.content.ContextCompat
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ItemListAssetBinding

fun ItemListAssetBinding.setupYieldTextColor(yield: Double) {
    val context = root.context
    if (yield > 0)
        tvYield.setTextColor(ContextCompat.getColor(context, R.color.green))
    else if (yield < 0)
        tvYield.setTextColor(ContextCompat.getColor(context, R.color.red))
    else
        tvYield.setTextColor(ContextCompat.getColor(context, R.color.gray))
}
