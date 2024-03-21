package com.omouravictor.invest_view.data.network.base

sealed class NetworkState<out T> {
    data object Loading : NetworkState<Nothing>()
    data class Success<out T>(val data: T) : NetworkState<T>()
    data class Error(val e: Exception) : NetworkState<Nothing>()
}
