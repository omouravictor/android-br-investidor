package com.omouravictor.wise_invest.presenter.news.article

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceUiModel(
    val name: String?
) : Parcelable