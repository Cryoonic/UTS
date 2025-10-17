package com.example.uts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uts.LoginActivity
import com.example.uts.databinding.FragmentProfileBinding
import com.example.uts.model.User
import com.example.uts.utils.SharedPref
import com.example.uts.utils.ToastType
import com.example.uts.utils.showCustomToast

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val b get() = _binding!!
    private lateinit var pref: SharedPref

    private var foodScans = 12
    private var daysActive = 5
    private var caloriesGoal = 2000
    private var proteinGoal = 50
    private var waterGoal = 2.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())

        loadUserData()
        setupStatsAndGoals()
        setupSettings()
        setupLogout()
        setupDeleteAccount()

        return b.root
    }

    private fun loadUserData() {
        val user = pref.getUser()
        b.tvUsername.text = user?.username ?: "Guest"
        b.etUsername.setText(user?.username ?: "")
        b.etEmail.setText(user?.email ?: "")
    }

    private fun setupStatsAndGoals() {

        b.tvFoodScans.text = "Scans: $foodScans"
        b.tvDaysActive.text = "Days Active: $daysActive"
        b.tvCaloriesGoal.text = "$caloriesGoal kcal"
        b.tvProteinGoal.text = "$proteinGoal g"
        b.tvWaterGoal.text = "$waterGoal L"
    }

    private fun setupSettings() {
        b.btnSaveSettings.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty()) {
                showCustomToast(requireContext(),"Isi semua field", ToastType.INFO)
                return@setOnClickListener
            }


            val oldUser = pref.getUser()
            val newUser = User(username, email)
            pref.saveUser(newUser)

            if (password.isNotEmpty()) {
                val editor = requireContext().getSharedPreferences("nutriscan_prefs", 0).edit()
                editor.putString("registered_user_password", password)
                editor.apply()
            }

            showCustomToast(requireContext(), "Profil disimpan", ToastType.SUCCESS)
        }
    }

    private fun setupLogout() {
        b.btnLogout.setOnClickListener {
            pref.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setupDeleteAccount() {
        b.btnDeleteAccount.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Hapus Akun")
                .setMessage("Apakah kamu yakin ingin menghapus akun ini? Semua data akan hilang permanen.")
                .setPositiveButton("Ya") { _, _ ->
                    pref.deleteAccount()
                    showCustomToast(requireContext(), "Akun berhasil dihapus", ToastType.SUCCESS)
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
