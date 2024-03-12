package com.omouravictor.invest_view.data.remote.base

sealed class RemoteResultStatus<out T> {
    data object Loading : RemoteResultStatus<Nothing>()
    data class Success<out T>(val data: T) : RemoteResultStatus<T>()
    data class Error(val message: String) : RemoteResultStatus<Nothing>()
}
