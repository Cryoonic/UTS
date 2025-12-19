package com.example.uts.dao

import android.health.connect.datatypes.MealType
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.uts.model.FoodHistory

@Dao
interface FoodHistoryDao {

    @Insert
    suspend fun insert(food: FoodHistory)

    // ✅ Ambil semua history per tanggal
    @Query("""
        SELECT * FROM food_history 
        WHERE username = :username 
        AND date(timestamp/1000, 'unixepoch') = date(:selectedDate/1000, 'unixepoch')
    """)
    suspend fun getByDate(
        username: String,
        selectedDate: Long
    ): List<FoodHistory>

    // ✅ Ambil total kalori per meal type (Breakfast, Lunch, Dinner, Snack)
    @Query("""
        SELECT IFNULL(SUM(calories), 0) FROM food_history 
        WHERE username = :username 
        AND mealType = :mealType 
        AND date(timestamp/1000, 'unixepoch') = date(:selectedDate/1000, 'unixepoch')
    """)
    suspend fun getMealCalories(
        username: String,
        mealType: String,
        selectedDate: Long
    ): Int
}
