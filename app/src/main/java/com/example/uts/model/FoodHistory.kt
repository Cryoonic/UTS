package com.example.uts.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_history")
data class FoodHistory (
    @PrimaryKey(true) val id: Int =0,
    val username: String,
    val foodname: String,
    val mealType: String,
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val fiber: Double,
    val timestamp: Long

)