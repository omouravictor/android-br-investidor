package com.omouravictor.invest_view.presenter.model

import android.os.Parcelable
import com.omouravictor.invest_view.data.remote.model.news.SourceResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleUiModel(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceResponse,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Parcelable