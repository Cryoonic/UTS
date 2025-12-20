package com.example.uts.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.uts.LoginActivity
import com.example.uts.databinding.FragmentProfileBinding
import com.example.uts.model.User
import com.example.uts.utils.HashUtil
import com.example.uts.utils.SharedPref
import com.example.uts.utils.ToastType
import com.example.uts.utils.showCustomToast
import com.example.uts.R
import com.example.uts.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val b get() = _binding!!
    private lateinit var pref: SharedPref
    private lateinit var db: AppDatabase
    private var currentUser: User? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()){
                uri ->
            uri?.let {
                saveProfileImage(it)
                cropImage(it)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()){bitmap ->
            bitmap?.let {
                val uri = ImageUtil.saveBitmap(requireContext(), it)
                saveProfileImage(uri)
            }
        }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())
        db = AppDatabase.getDatabase(requireContext())

        loadUserData()
        setupSettings()
        setupLogout()
        setupDeleteAccount()
        b.imgProfile.setOnClickListener {
            showImagePickerDialog()
        }

        return b.root
    }

    private fun loadUserData() {
        val username = pref.getSession() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val user = db.userDao().getUser(username)
            currentUser = user

            withContext(Dispatchers.Main){
                b.tvUsername.text = user?.username ?: "Guest"
                b.etUsername.setText(user?.username ?: "")
                b.etEmail.setText(user?.email ?: "")

                if (!user?.profileImageUri.isNullOrEmpty()){
                    Glide.with(this@ProfileFragment)
                        .load(user?.profileImageUri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(b.imgProfile)
                }
            }
        }
    }

    private fun showImagePickerDialog(){
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Pilih Foto Profil")
            .setItems(arrayOf("Galeri", "Camera")){_, which ->
                when(which){
                    0 -> imagePickerLauncher.launch("image/*")
                    1 -> openCamera()
                }
            }.show()
    }

    private fun openCamera(){
        cameraLauncher.launch(null)
    }
    private fun saveProfileImage(uri: Uri){
        val username = pref.getSession() ?: return
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(b.imgProfile)
        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().updateProfileImage(username, uri.toString())
        }
    }



    private val cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.getParcelable<Bitmap>("data")
                bitmap?.let {
                    val uri = ImageUtil.saveBitmap(requireContext(), it)
                    saveProfileImage(uri)
                }
            }
        }

    private fun cropImage(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP").apply {
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("aspectX", 1)
            putExtra("aspectY", 1)
            putExtra("outputX", 300)
            putExtra("outputY", 300)
            putExtra("return-data", true)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        cropLauncher.launch(intent)
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

    object ImageUtil{
        fun saveBitmap(context: Context, bitmap: Bitmap): Uri{
            val file = File(context.cacheDir,"profile_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        }
    }
}
