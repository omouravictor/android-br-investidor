package com.omouravictor.br_investidor.di.model

import com.google.common.cache.CacheBuilder
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class RequestCacheInterceptor(
    duration: Long = 1,
    timeUnit: TimeUnit = TimeUnit.MINUTES
) : Interceptor {

    private val requestCache = CacheBuilder.newBuilder()
        .expireAfterWrite(duration, timeUnit)
        .build<String, Boolean>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestKey = request.url.toString()

        return if (requestCache.getIfPresent(requestKey) != null) {
            val cachedRequest = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()

            val cachedResponse = chain.proceed(cachedRequest)

            if (cachedResponse.code != CACHE_NOT_FOUND_CODE)
                cachedResponse
            else
                chain.proceed(request)

        } else {
            val response = chain.proceed(request)
            requestCache.put(requestKey, true)
            response
        }
    }

    companion object {
        const val CACHE_NOT_FOUND_CODE = 504
    }
}