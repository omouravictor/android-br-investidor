package com.omouravictor.invest_view.presenter.wallet

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.asset_quote.model.AssetQuoteUiModel
import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetTypes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetDTO(
    var assetBySearchUiModel: AssetBySearchUiModel? = null,
    var assetQuoteUiModel: AssetQuoteUiModel? = null,
    var assetTypes: AssetTypes = AssetTypes.UNKNOWN,
) : Parcelable