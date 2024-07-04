package com.omouravictor.invest_view.presenter.wallet.model

sealed class UiState<out T> {
    data object Initial : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val e: Exception) : UiState<Nothing>()
}
