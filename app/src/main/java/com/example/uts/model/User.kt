package com.example.uts.model

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val profileImageUri: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val activityLevel: String? = null
)
