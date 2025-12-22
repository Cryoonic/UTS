package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityLoginBinding
import com.example.uts.model.User
import com.example.uts.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        b.btnLogin.setOnClickListener {

            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val uid = auth.currentUser!!.uid

                    db.collection("users")
                        .document(uid) // 🔥 SESUAI RULES
                        .get()
                        .addOnSuccessListener { doc ->

                            if (!doc.exists()) {
                                Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            val user = doc.toObject(User::class.java)!!

                            SharedPref(this).saveSession(user.username)

                            if (
                                user.gender == null ||
                                user.age == null ||
                                user.height == null ||
                                user.weight == null ||
                                user.activityLevel == null
                            ) {
                                startActivity(Intent(this, ProfileSetupActivity::class.java))
                            } else {
                                startActivity(Intent(this, MainActivity::class.java))
                            }

                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show()
                }
        }


        b.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
