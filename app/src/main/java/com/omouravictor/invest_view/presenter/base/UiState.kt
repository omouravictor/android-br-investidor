package com.omouravictor.invest_view.presenter.base

sealed class UiState<out T> {
    data object Empty : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
