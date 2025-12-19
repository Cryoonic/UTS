package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityRegisterBinding
import com.example.uts.model.User
import com.example.uts.utils.HashUtil
import com.example.uts.utils.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.uts.database.AppDatabase


class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = AppDatabase.getDatabase(this)

        b.btnRegister.setOnClickListener {

            val username = b.etUsername.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Lengkapi semua data!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordHash = HashUtil.sha256(password)

            val user = User(
                username = username,
                email = email,
                passwordHash = passwordHash   // ✔ sesuai model
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.userDao().insertUser(user)

                runOnUiThread {
                    SharedPref(this@RegisterActivity).saveSession(username)

                    val i = Intent(this@RegisterActivity, ProfileSetupActivity::class.java)
                    i.putExtra("username", username)
                    startActivity(i)
                    finish()
                }
            }
        }
        b.btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}