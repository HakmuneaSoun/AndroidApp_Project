package com.example.final_project.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val retryDelayMs: Long = 2_000L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null

        repeat(maxRetries) { attempt ->
            try {
                return chain.proceed(request)
            } catch (e: IOException) {
                lastException = e
                val shouldRetry = attempt < maxRetries - 1 && isRetryable(e)
                if (shouldRetry) {
                    Thread.sleep(retryDelayMs * (attempt + 1))
                }
            }
        }

        throw lastException ?: IOException("Request failed")
    }

    private fun isRetryable(exception: IOException): Boolean {
        return exception is SocketTimeoutException ||
            exception is UnknownHostException ||
            exception.message?.contains("timeout", ignoreCase = true) == true ||
            exception.message?.contains("failed to connect", ignoreCase = true) == true ||
            exception.message?.contains("connection reset", ignoreCase = true) == true
    }
}
