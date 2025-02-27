package com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model

import com.google.gson.annotations.SerializedName
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel
import com.omouravictor.br_investidor.util.AssetUtil

data class BestMatchResponse(
    @SerializedName("1. symbol") val symbol: String?,
    @SerializedName("2. name") val name: String?,
    @SerializedName("3. type") val type: String?,
    @SerializedName("4. region") val region: String?,
    @SerializedName("5. marketOpen") val marketOpen: String?,
    @SerializedName("6. marketClose") val marketClose: String?,
    @SerializedName("7. timezone") val timezone: String?,
    @SerializedName("8. currency") val currency: String?,
    @SerializedName("9. matchScore") val matchScore: String?
)

data class AssetsBySearchResponse(
    val bestMatches: List<BestMatchResponse>
)

fun AssetsBySearchResponse.toAssetsUiModel(): List<AssetUiModel> {
    return bestMatches.map {
        AssetUiModel(
            symbol = it.symbol ?: "",
            name = it.name ?: "",
            type = AssetUtil.getAssetType(it.symbol ?: "", it.type ?: ""),
            region = it.region ?: "",
            currency = it.currency ?: ""
        )
    }
}