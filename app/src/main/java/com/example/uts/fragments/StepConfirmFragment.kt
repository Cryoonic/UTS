package com.example.uts.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uts.viewmodel.ProfileViewModel
import com.example.uts.databinding.FragmentStepConfirmBinding
import android.widget.Toast
import com.example.uts.LoginActivity
import com.example.uts.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StepConfirmFragment : Fragment() {

    private lateinit var b: FragmentStepConfirmBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = FragmentStepConfirmBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val vm = ProfileViewModel.instance

        // 🔹 Tampilkan ringkasan
        b.tvSummary.text = """
        Age: ${vm.age}
        Height: ${vm.height}
        Weight: ${vm.weight}
        Gender: ${vm.gender}
        Activity: ${vm.activity}
    """.trimIndent()

        b.btnSave.setOnClickListener {

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(
                    requireContext(),
                    "Sesi tidak ditemukan, silakan login ulang",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val uid = currentUser.uid

            val updates = hashMapOf<String, Any>()
            vm.age?.let { updates["age"] = it }
            vm.height?.let { updates["height"] = it }
            vm.weight?.let { updates["weight"] = it }
            vm.gender?.let { updates["gender"] = it }
            vm.activity?.let { updates["activityLevel"] = it }

            firestore.collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener {

                    Toast.makeText(
                        requireContext(),
                        "Profil berhasil disimpan, silakan login ulang",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 🔥 LOGOUT FIREBASE
                    auth.signOut()

                    // 🔥 PINDAH KE LOGIN
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Gagal menyimpan profil: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        return b.root
    }
}
