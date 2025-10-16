package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityRegisterBinding
import com.example.uts.model.User
import com.example.uts.utils.SharedPref

class RegisterActivity : AppCompatActivity() {
    private lateinit var b: ActivityRegisterBinding
    private lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)
        pref = SharedPref(this)

        b.btnRegister.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            } else {
                val editor = getSharedPreferences("nutriscan_prefs", MODE_PRIVATE).edit()
                editor.putString("registered_user_username", username)
                editor.putString("registered_user_email", email)
                editor.putString("registered_user_password", pass)
                editor.apply()

                Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        b.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
