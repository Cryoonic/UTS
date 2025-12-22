package com.example.uts.model

data class FoodHistory(
    val uid: String = "",
    val username: String ="",
    val foodname: String = "",
    val mealType: String = "",
    val calories: Int = 0,
    val carbs: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val timestamp: Long = 0L
)
