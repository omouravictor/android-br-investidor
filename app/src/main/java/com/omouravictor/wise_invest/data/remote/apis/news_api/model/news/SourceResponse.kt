package com.omouravictor.wise_invest.data.remote.apis.news_api.model.news

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceResponse(
    val id: String?,
    val name: String?
) : Parcelable