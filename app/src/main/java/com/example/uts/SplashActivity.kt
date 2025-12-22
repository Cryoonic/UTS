package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser

            // ➤ Jika belum login → ke Login
            if (currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@postDelayed
            }

            // ➤ Jika sudah login → cek ke Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        val gender = document.getString("gender")
                        val age = document.getLong("age")
                        val height = document.getLong("height")
                        val weight = document.getLong("weight")
                        val activityLevel = document.getString("activityLevel")

                        // ➤ Jika profil belum lengkap → buka ProfileSetup
                        if (gender == null || age == null || height == null || weight == null || activityLevel == null) {
                            val i = Intent(this@SplashActivity, ProfileSetupActivity::class.java)
                            if (username != null) {
                                i.putExtra("username", username)
                            }
                            startActivity(i)
                        } else {
                            // ➤ Profil lengkap → langsung ke Main
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        }
                    } else {
                        // Jika dokumen tidak ada, arahkan ke Login
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener {
                    // Gagal mengambil data, arahkan ke Login
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
        }, 1200)
    }
}
