package com.omouravictor.invest_view.data.remote.model.news

import com.omouravictor.invest_view.presenter.model.SourceUiModel

data class SourceResponse(
    val id: String?,
    val name: String?
)

fun SourceResponse.toSourceUiModel(): SourceUiModel {
    return SourceUiModel(
        id = this.id,
        name = this.name
    )
}