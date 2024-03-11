package com.omouravictor.invest_view.di

import okhttp3.Interceptor
import okhttp3.Response

class Interceptor(
    private val apiKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url

        val newUrl = url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()

        return chain.proceed(
            request.newBuilder()
                .url(newUrl)
                .build()
        )
    }

}