package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.database.AppDatabase
import com.example.uts.utils.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val username = SharedPref(this).getSession()

            // ➤ Jika belum login → ke Login
            if (username == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@postDelayed
            }

            // ➤ Jika sudah login → cek ke database
            CoroutineScope(Dispatchers.IO).launch {
                val user = AppDatabase.getDatabase(this@SplashActivity)
                    .userDao()
                    .getUser(username)

                runOnUiThread {
                    if (user == null) {
                        // Session tidak valid → logout
                        SharedPref(this@SplashActivity).logout()
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                        return@runOnUiThread
                    }

                    // ➤ Jika profil belum lengkap → buka ProfileSetup
                    if (user.gender == null ||
                        user.age == null ||
                        user.height == null ||
                        user.weight == null ||
                        user.activityLevel == null) {

                        val i = Intent(this@SplashActivity, ProfileSetupActivity::class.java)
                        i.putExtra("username", username)
                        startActivity(i)
                    } else {
                        // ➤ Profil lengkap → langsung ke Main
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }

                    finish()
                }
            }
        }, 1200)
    }
}
