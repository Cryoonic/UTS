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
import com.example.uts.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepConfirmFragment : Fragment() {

    private lateinit var b: FragmentStepConfirmBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentStepConfirmBinding.inflate(inflater, container, false)

        val vm = ProfileViewModel.instance

        // tampil ringkasan
        b.tvSummary.text = """
            Age: ${vm.age}
            Height: ${vm.height}
            Weight: ${vm.weight}
            Gender: ${vm.gender}
            Activity: ${vm.activity}
        """.trimIndent()

        b.btnSave.setOnClickListener {

            val username = requireActivity()
                .intent.getStringExtra("username") ?: return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getDatabase(requireContext()).userDao()
                val user = dao.getUser(username) ?: return@launch

                val updated = user.copy(
                    age = vm.age,
                    height = vm.height,
                    weight = vm.weight,
                    gender = vm.gender,
                    activityLevel = vm.activity
                )

                dao.updateUser(updated)

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Profil diperbarui", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        return b.root
    }
}
