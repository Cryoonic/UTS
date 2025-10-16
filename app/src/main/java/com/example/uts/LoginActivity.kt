package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityLoginBinding
import com.example.uts.model.User
import com.example.uts.utils.SharedPref

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        pref = SharedPref(this)

        if (pref.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish(); return
        }

        b.btnLogin.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()

            if (username.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show()
            } else {
                val prefs = getSharedPreferences("nutriscan_prefs", MODE_PRIVATE)
                val savedUsername = prefs.getString("registered_user_username", null)
                val savedEmail = prefs.getString("registered_user_email", null)
                val savedPassword = prefs.getString("registered_user_password", null)

                if (username == savedUsername && pass == savedPassword) {
                    pref.saveUser(User(savedUsername ?: username, savedEmail ?: ""))
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Username atau password salah.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        b.btnRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}
