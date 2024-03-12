package com.omouravictor.invest_view.data.network.base

sealed class NetworkResultStatus<out T> {
    data object Loading : NetworkResultStatus<Nothing>()
    data class Success<out T>(val data: T) : NetworkResultStatus<T>()
    data class Error(val message: String) : NetworkResultStatus<Nothing>()
}
