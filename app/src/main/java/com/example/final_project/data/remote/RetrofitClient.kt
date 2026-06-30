package com.example.final_project.data.remote

import android.content.Context
import com.example.final_project.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private lateinit var sessionManager: SessionManager

    fun init(context: Context) {
        if (::sessionManager.isInitialized) return
        sessionManager = SessionManager(context.applicationContext)
    }

    fun getSessionManager(): SessionManager = requireSessionManager()

    private fun requireSessionManager(): SessionManager {
        check(::sessionManager.isInitialized) {
            "RetrofitClient.init(context) must be called before using APIs"
        }
        return sessionManager
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .addInterceptor(AuthInterceptor(requireSessionManager()))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    suspend fun warmupServer() = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConstants.BASE_URL}api/tours")
                .get()
                .build()
            okHttpClient.newCall(request).execute().use { it.close() }
        } catch (_: Exception) {
            // Login and other screens will retry automatically.
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val profileApi: ProfileApiService by lazy { retrofit.create(ProfileApiService::class.java) }
    val fileApi: FileApiService by lazy { retrofit.create(FileApiService::class.java) }
    val adminApi: AdminApiService by lazy { retrofit.create(AdminApiService::class.java) }
    val tourApi: TourApiService by lazy { retrofit.create(TourApiService::class.java) }
    val categoryApi: CategoryApiService by lazy { retrofit.create(CategoryApiService::class.java) }
    val favoriteApi: FavoriteApiService by lazy { retrofit.create(FavoriteApiService::class.java) }
    val bookingApi: BookingApiService by lazy { retrofit.create(BookingApiService::class.java) }
    val paymentApi: PaymentApiService by lazy { retrofit.create(PaymentApiService::class.java) }
    val reviewApi: ReviewApiService by lazy { retrofit.create(ReviewApiService::class.java) }
}
