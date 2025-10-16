package com.example.uts.model

data class Food(
    val name: String,
    val description: String,
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val imageResId: Int = 0
)
