package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.AddFavoriteRequest
import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.FavoriteCheckData
import com.example.final_project.data.remote.dto.FavoriteTourData
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApiService {

    @GET("api/favorites")
    suspend fun getFavorites(): ApiResponse<List<FavoriteTourData>>

    @POST("api/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): ApiResponse<FavoriteTourData>

    @GET("api/favorites/check/{tourId}")
    suspend fun checkFavorite(@Path("tourId") tourId: Long): ApiResponse<FavoriteCheckData>

    @DELETE("api/favorites/tour/{tourId}")
    suspend fun removeFavorite(@Path("tourId") tourId: Long): ApiResponse<Any?>
}
