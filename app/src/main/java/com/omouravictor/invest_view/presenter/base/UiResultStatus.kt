package com.omouravictor.invest_view.presenter.base

sealed class UiResultStatus<out T> {
    data object Loading : UiResultStatus<Nothing>()
    data class Success<out T>(val data: T) : UiResultStatus<T>()
    data class Error(val message: String) : UiResultStatus<Nothing>()
}
