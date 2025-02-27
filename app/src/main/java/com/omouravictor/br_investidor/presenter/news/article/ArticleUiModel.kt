package com.omouravictor.br_investidor.presenter.news.article

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleUiModel(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceUiModel,
    val title: String?,
    val url: String,
    val urlToImage: String?
) : Parcelable