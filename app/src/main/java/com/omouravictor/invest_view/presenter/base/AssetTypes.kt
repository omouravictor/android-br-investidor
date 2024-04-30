package com.omouravictor.invest_view.presenter.base

import com.omouravictor.invest_view.R

enum class AssetTypes(val nameResId: Int, val colorResId: Int) {

    LOCAL_STOCK(R.string.localStock, R.color.localStock),
    LOCAL_DEPOSITARY_RECEIPTS(R.string.localDepositaryReceipts, R.color.localDepositaryReceipts),
    FI(R.string.fi, R.color.fi),
    ETF(R.string.etf, R.color.etf),
    STOCK(R.string.stock, R.color.stock),
    OTHER(R.string.other, R.color.other);

}