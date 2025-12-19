package com.example.uts

import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionApi {

    @GET("foods/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 1,
        @Query("api_key") apiKey: String
    ): NutritionResponse
}
