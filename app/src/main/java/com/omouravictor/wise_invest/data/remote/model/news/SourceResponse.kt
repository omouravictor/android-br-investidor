package com.omouravictor.wise_invest.data.remote.model.news

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceResponse(
    val id: String?,
    val name: String?
) : Parcelable