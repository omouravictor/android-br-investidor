package com.omouravictor.br_investidor.presenter.news.article

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceUiModel(
    val name: String?
) : Parcelable