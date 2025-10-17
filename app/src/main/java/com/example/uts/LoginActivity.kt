package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityLoginBinding
import com.example.uts.model.User
import com.example.uts.utils.SharedPref
import com.example.uts.utils.ToastType
import com.example.uts.utils.showCustomToast

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
               showCustomToast(this, "Isi semua data", ToastType.INFO)
            } else {
                val prefs = getSharedPreferences("nutriscan_prefs", MODE_PRIVATE)
                val savedUsername = prefs.getString("registered_user_username", null)
                val savedEmail = prefs.getString("registered_user_email", null)
                val savedPassword = prefs.getString("registered_user_password", null)

                if (username == savedUsername && pass == savedPassword) {
                    pref.saveUser(User(savedUsername ?: username, savedEmail ?: ""))
                    showCustomToast(this,"Login berhasil", ToastType.SUCCESS)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showCustomToast(this, "Username atau password salah.", ToastType.ERROR)
                }
            }
        }


        b.btnRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}
