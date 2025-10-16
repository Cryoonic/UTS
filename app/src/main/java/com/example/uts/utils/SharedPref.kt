package com.example.uts.utils

import android.content.Context
import com.example.uts.model.Food
import com.example.uts.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPref(context: Context) {
    private val prefs = context.getSharedPreferences("nutriscan_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun saveUser(user: User) {
        prefs.edit().putString("user_data", gson.toJson(user)).putBoolean("is_logged_in", true).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString("user_data", null) ?: return null
        return gson.fromJson(json, User::class.java)
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun logout() {
        prefs.edit()
            .remove("user_data")
            .remove("is_logged_in")
            .apply()
    }

    fun getHistoryList(): MutableList<Food> {
        val json = prefs.getString("history_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Food>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveHistoryList(list: List<Food>) {
        prefs.edit().putString("history_list", gson.toJson(list)).apply()
    }

    fun addHistoryItem(item: Food) {
        val list = getHistoryList()
        list.add(0, item) // newest first
        saveHistoryList(list)
    }
}
