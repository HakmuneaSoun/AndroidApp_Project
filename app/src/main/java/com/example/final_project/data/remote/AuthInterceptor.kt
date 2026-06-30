package com.example.final_project.data.remote

import com.example.final_project.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val isPublicAuth = path.endsWith("/api/auth/login") || path.endsWith("/api/auth/register")

        val token = sessionManager.getToken()
        val authedRequest = if (!isPublicAuth && !token.isNullOrBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(authedRequest)
    }
}
