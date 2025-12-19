package com.example.uts

data class NutritionResponse(
    val foods: List<FoodItem>
)

data class FoodItem(
    val description: String,
    val foodNutrients: List<Nutrient>
)

data class Nutrient(
    val nutrientName: String,
    val value: Double
)