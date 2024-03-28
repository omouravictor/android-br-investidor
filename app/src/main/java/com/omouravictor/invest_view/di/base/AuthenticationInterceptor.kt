package com.omouravictor.invest_view.di.base

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder().addHeader("X-RapidAPI-Key", apiKey).build()

        return chain.proceed(newRequest)
    }

}