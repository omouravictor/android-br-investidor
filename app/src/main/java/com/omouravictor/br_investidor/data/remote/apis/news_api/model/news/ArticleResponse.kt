package com.omouravictor.br_investidor.data.remote.apis.news_api.model.news

import com.omouravictor.br_investidor.presenter.news.article.SourceUiModel

data class SourceResponse(
    val id: String?,
    val name: String?
)

data class ArticleResponse(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceResponse,
    val title: String?,
    val url: String,
    val urlToImage: String?
)

fun SourceResponse.toSourceUiModel(): SourceUiModel {
    return SourceUiModel(
        name = name
    )
}