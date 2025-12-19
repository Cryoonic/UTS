package com.example.uts.utils

import android.content.Context

class SharedPref(context: Context) {

    private val prefs = context.getSharedPreferences("nutriscan_session", Context.MODE_PRIVATE)

    /** Simpan sesi login (username + flag) */
    fun saveSession(username: String) {
        prefs.edit()
            .putString("session_username", username)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    /** Ambil username yang sedang login */
    fun getSession(): String? {
        return prefs.getString("session_username", null)
    }

    /** Cek apakah user sudah login */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    /** Logout -> hapus seluruh session */
    fun logout() {
        prefs.edit().clear().apply()
    }
}
