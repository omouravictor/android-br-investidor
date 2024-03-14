package com.omouravictor.invest_view.presenter.base

sealed class UiResultState<out T> {
    data object Loading : UiResultState<Nothing>()
    data class Success<out T>(val data: T) : UiResultState<T>()
    data class Error(val message: String) : UiResultState<Nothing>()
}
