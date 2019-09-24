package com.bd.deliverytiger.app.api

import com.bd.deliverytiger.app.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${SessionManager.accessToken}")
            .build()
        return chain.proceed(request)
    }
}