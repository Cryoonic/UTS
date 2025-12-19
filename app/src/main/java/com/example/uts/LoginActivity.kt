package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityLoginBinding
import com.example.uts.utils.HashUtil
import com.example.uts.utils.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.uts.database.AppDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.getDatabase(this)

        b.btnLogin.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val password = b.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordHash = HashUtil.sha256(password)

            CoroutineScope(Dispatchers.IO).launch {
                val user = db.userDao().login(username, passwordHash)

                runOnUiThread {
                    if (user == null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Username atau password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        // Simpan session
                        SharedPref(this@LoginActivity).saveSession(user.username)

                        // ➤ Cek apakah profil sudah lengkap
                        if (user.gender == null ||
                            user.age == null ||
                            user.height == null ||
                            user.weight == null ||
                            user.activityLevel == null) {

                            // Belum lengkap → ke Setup Profile
                            val i = Intent(this@LoginActivity, ProfileSetupActivity::class.java)
                            i.putExtra("username", user.username)
                            startActivity(i)
                        } else {
                            // Lengkap → ke Main
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }

                        finish()
                    }
                }
            }
        }

        b.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
