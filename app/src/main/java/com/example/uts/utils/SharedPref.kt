package com.example.uts.utils

import android.content.Context
import com.example.uts.model.Food
import com.example.uts.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPref(context: Context) {
    //Inialisasi SharedPreferences
    private val prefs = context.getSharedPreferences("nutriscan_prefs", Context.MODE_PRIVATE)
    //untuk mengubah objek menjadi Json atau sebaliknyaa
    private val gson = Gson()

    //menyimpan data user dalam format Json
    fun saveUser(user: User) {
        prefs.edit().putString("user_data", gson.toJson(user)).putBoolean("is_logged_in", true).apply()
    }

    //mengambil data Json dan membuatnya menjadi objek user
    fun getUser(): User? {
        val json = prefs.getString("user_data", null) ?: return null
        return gson.fromJson(json, User::class.java)
    }

    //cek apakah user sedang login?
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    //menghapus data pengguna dan status loginnya
    fun logout() {
        prefs.edit()
            .remove("user_data")
            .remove("is_logged_in")
            .apply()
    }

    //menghapus seluruh data dari Shared preferences
    fun deleteAccount() {
        prefs.edit()
            .clear()
            .apply()
    }

    //mengambil history makanan dari SharedPref
    fun getHistoryList(): MutableList<Food> {
        val json = prefs.getString("history_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Food>>() {}.type
        return gson.fromJson(json, type)
    }

    //menyimpan riwayat makanan ke SharedPref dalam bentuk Json
    fun saveHistoryList(list: List<Food>) {
        prefs.edit().putString("history_list", gson.toJson(list)).apply()
    }

    //menambah list
    fun addHistoryItem(item: Food) {
        val list = getHistoryList()
        list.add(0, item)
        saveHistoryList(list)
    }
}
