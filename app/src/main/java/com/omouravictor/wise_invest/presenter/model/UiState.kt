package com.omouravictor.wise_invest.presenter.model

sealed class UiState<out T> {
    data object Initial : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val e: Exception) : UiState<Nothing>()
}
