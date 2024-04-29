package com.omouravictor.invest_view.presenter.base

import android.content.Context
import androidx.core.content.ContextCompat
import com.omouravictor.invest_view.R

enum class AssetTypes(private val nameResId: Int, private val colorResId: Int) {

    LOCAL_STOCK(R.string.localStock, R.color.localStock),
    LOCAL_DEPOSITARY_RECEIPTS(R.string.localDepositaryReceipts, R.color.localDepositaryReceipts),
    FI(R.string.fi, R.color.fi),
    ETF(R.string.etf, R.color.etf),
    STOCK(R.string.stock, R.color.stock),
    OTHER(R.string.other, R.color.other);

    fun getName(context: Context) = context.getString(nameResId)
    fun getColor(context: Context) = ContextCompat.getColor(context, colorResId)
    fun getColorStateList(context: Context) = ContextCompat.getColorStateList(context, colorResId)!!
}