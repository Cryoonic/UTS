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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.uts.LoginActivity
import com.example.uts.databinding.FragmentProfileBinding
import com.example.uts.model.User
import com.example.uts.utils.HashUtil
import com.example.uts.utils.SharedPref
import com.example.uts.utils.ToastType
import com.example.uts.utils.showCustomToast
import com.example.uts.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private lateinit var pref: SharedPref
    private var currentUser: User? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage


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


        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserData()
        setupSettings()
        setupLogout()
        setupDeleteAccount()

        binding!!.imgProfile.setOnClickListener {
            showImagePickerDialog()
        }

        return binding!!.root
    }

    private fun loadUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) return@addOnSuccessListener

                currentUser = document.toObject(User::class.java)

                binding!!.tvUsername.text = currentUser?.username ?: "Guest"
                binding!!.etUsername.setText(currentUser?.username ?: "")
                binding!!.etEmail.setText(currentUser?.email ?: "")

                val photoUrl = document.getString("profileImageUrl")
                if (!photoUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(binding!!.imgProfile)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed load profile", Toast.LENGTH_SHORT).show()
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
    private fun saveProfileImage(uri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding!!.imgProfile)

        val imageRef = storage.reference
            .child("profile_images/$uid.jpg") // ✅ UID

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    firestore.collection("users")
                        .document(uid)
                        .update("profileImageUrl", downloadUrl.toString())
                }
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
        binding!!.btnSaveSettings.setOnClickListener {

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            val updateData = hashMapOf<String, Any>(
                "username" to binding!!.etUsername.text.toString().trim(),
                "email" to binding!!.etEmail.text.toString().trim()
            )

            firestore.collection("users")
                .document(uid)
                .update(updateData)
                .addOnSuccessListener {
                    showCustomToast(requireContext(), "Profil disimpan", ToastType.SUCCESS)
                }
        }
    }


    private fun setupLogout() {
        binding!!.btnLogout.setOnClickListener {
            pref.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setupDeleteAccount() {
        binding!!.btnDeleteAccount.setOnClickListener {

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { snapshot ->

                    snapshot.getString("profileImageUrl")?.let {
                        storage.getReferenceFromUrl(it).delete()
                    }

                    firestore.collection("users")
                        .document(uid)
                        .delete()
                        .addOnSuccessListener {
                            FirebaseAuth.getInstance().currentUser?.delete()
                            pref.logout()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }
                }
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
