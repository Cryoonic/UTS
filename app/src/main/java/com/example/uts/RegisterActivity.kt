package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityRegisterBinding
import com.example.uts.model.User
import com.example.uts.utils.SharedPref
import com.example.uts.utils.ToastType
import com.example.uts.utils.showCustomToast

class RegisterActivity : AppCompatActivity() {
    // View binding untuk akses view di activity_register.xml
    private lateinit var b: ActivityRegisterBinding
    private lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)
        pref = SharedPref(this)

        // Tombol Register
        b.btnRegister.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                // Jika ada field kosong, tampilkan info
                showCustomToast(this,"Lengkapi semua data", ToastType.INFO)
            } else {
                // Simpan data user di SharedPreferences
                val editor = getSharedPreferences("nutriscan_prefs", MODE_PRIVATE).edit()
                editor.putString("registered_user_username", username)
                editor.putString("registered_user_email", email)
                editor.putString("registered_user_password", pass)
                editor.apply()

                // Notifikasi berhasil dan arahkan ke LoginActivity
                showCustomToast(this, "Registrasi berhasil! Silakan login.", ToastType.SUCCESS)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Tombol Login langsung arahkan ke LoginActivity
        b.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
