package com.omouravictor.wise_invest.presenter.news.article

import android.os.Parcelable
import com.omouravictor.wise_invest.data.remote.model.news.SourceResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleUiModel(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceResponse,
    val title: String?,
    val url: String,
    val urlToImage: String?
) : Parcelable