package com.omouravictor.invest_view.data.network.base

sealed class NetworkResultState<out T> {
    data object Loading : NetworkResultState<Nothing>()
    data class Success<out T>(val data: T) : NetworkResultState<T>()
    data class Error(val e: Exception) : NetworkResultState<Nothing>()
}
