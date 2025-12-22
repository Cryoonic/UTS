package com.example.uts.viewmodel.api

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class MLApi {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val BASE_URL = "http://10.0.2.2:5000"

    fun predictFood(imageFile: File, callback: (PredictionResult?) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("$BASE_URL/predict")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(PredictionResult(false, null, null, null, e.message))
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                try {
                    val result = gson.fromJson(json, PredictionResult::class.java)
                    callback(result)
                } catch (e: Exception) {
                    callback(PredictionResult(false, null, null, null, e.message))
                }
            }
        })
    }

    fun getAllFoods(callback: (FoodsResponse?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/foods")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                try {
                    val result = gson.fromJson(json, FoodsResponse::class.java)
                    callback(result)
                } catch (e: Exception) {
                    callback(null)
                }
            }
        })
    }

    fun getFoodByName(foodName: String, callback: (FoodNutrition?) -> Unit) {
        getAllFoods { response ->
            if (response != null) {
                val food = response.foods[foodName.lowercase()]
                callback(food)
            } else {
                callback(null)
            }
        }
    }

    fun checkHealth(callback: (Boolean) -> Unit) {
        val request = Request.Builder().url("$BASE_URL/health").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }
            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }

    data class PredictionResult(
        val success: Boolean,
        val prediction: String?,
        val confidence: Double?,
        val nutrition: FoodNutrition?,
        val error: String?
    )

    data class FoodsResponse(
        val total_foods: Int,
        val foods: Map<String, FoodNutrition>
    )

    data class FoodNutrition(
        val calories: Any,
        val protein: Any,
        val carbs: Any,
        val fat: Any,
        val serving_size: String
    )
}