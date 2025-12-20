package com.example.uts.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val profileImageUri: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val activityLevel: String? = null
)
