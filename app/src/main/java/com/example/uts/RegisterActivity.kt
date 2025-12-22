package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        b.btnRegister.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val uid = auth.currentUser!!.uid

                    val userData = hashMapOf(
                        "uid" to uid,
                        "username" to username,
                        "email" to email,
                        "profileImageUrl" to "",
                        "gender" to "",
                        "age" to 0,
                        "height" to 0,
                        "weight" to 0,
                        "activityLevel" to ""
                    )

                    firestore.collection("users")
                        .document(uid) // 🔥 SELALU UID
                        .set(userData)
                        .addOnSuccessListener {

                            val i = Intent(this, ProfileSetupActivity::class.java)
                            i.putExtra("uid", uid)
                            startActivity(i)
                            finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Gagal simpan user: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Register gagal: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }

        b.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
