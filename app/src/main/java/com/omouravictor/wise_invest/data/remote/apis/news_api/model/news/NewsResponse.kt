package com.omouravictor.wise_invest.data.remote.apis.news_api.model.news

import com.omouravictor.wise_invest.presenter.news.article.ArticleUiModel

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleResponse>
)

fun NewsResponse.toNewsUiModel(): List<ArticleUiModel> {
    return articles.map {
        ArticleUiModel(
            author = it.author,
            content = it.content,
            description = it.description,
            publishedAt = it.publishedAt,
            source = it.source.toSourceUiModel(),
            title = it.title,
            url = it.url,
            urlToImage = it.urlToImage
        )
    }
}