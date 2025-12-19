package com.example.uts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.uts.LoginActivity
import com.example.uts.databinding.FragmentProfileBinding
import com.example.uts.model.User
import com.example.uts.utils.HashUtil
import com.example.uts.utils.SharedPref
import com.example.uts.utils.ToastType
import com.example.uts.utils.showCustomToast
import com.example.uts.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val b get() = _binding!!
    private lateinit var pref: SharedPref
    private lateinit var db: AppDatabase
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())
        db = AppDatabase.getDatabase(requireContext())

        loadUserData()
        setupStatsAndGoals()
        setupSettings()
        setupLogout()
        setupDeleteAccount()

        return b.root
    }

    private fun loadUserData() {
        val username = pref.getSession() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val user = db.userDao().getUser(username)
            currentUser = user

            requireActivity().runOnUiThread {
                b.tvUsername.text = user?.username ?: "Guest"
                b.etUsername.setText(user?.username ?: "")
                b.etEmail.setText(user?.email ?: "")
            }
        }
    }

    private fun setupStatsAndGoals() {
        b.tvFoodScans.text = "Scans: 12"
        b.tvDaysActive.text = "Days Active: 5"
        b.tvCaloriesGoal.text = "2000 kcal"
        b.tvProteinGoal.text = "50 g"
        b.tvWaterGoal.text = "2.0 L"
    }

    private fun setupSettings() {
        b.btnSaveSettings.setOnClickListener {

            val newUsername = b.etUsername.text.toString().trim()
            val newEmail   = b.etEmail.text.toString().trim()
            val newPassword = b.etPassword.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                showCustomToast(requireContext(), "Isi semua field", ToastType.INFO)
                return@setOnClickListener
            }

            val oldUser = currentUser ?: return@setOnClickListener

            val updatedUser = oldUser.copy(
                username = newUsername,
                email = newEmail,
                passwordHash = if (newPassword.isNotEmpty())
                    HashUtil.sha256(newPassword)
                else
                    oldUser.passwordHash
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.userDao().updateUser(updatedUser)

                requireActivity().runOnUiThread {
                    pref.saveSession(newUsername)
                    showCustomToast(requireContext(), "Profil disimpan", ToastType.SUCCESS)
                }
            }
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
                .setMessage("Yakin ingin menghapus akun ini?")
                .setPositiveButton("Ya") { _, _ ->

                    val username = pref.getSession() ?: return@setPositiveButton

                    CoroutineScope(Dispatchers.IO).launch {
                        db.userDao().deleteUser(username)

                        requireActivity().runOnUiThread {
                            pref.logout()
                            showCustomToast(requireContext(), "Akun berhasil dihapus", ToastType.SUCCESS)
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
