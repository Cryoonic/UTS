package com.example.uts.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.uts.databinding.FragmentScanBinding
import kotlin.random.Random

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val b get() = _binding!!

    // ✅ Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            showDeniedDialog()
        }
    }

    // ✅ Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            b.ivPreview.setImageBitmap(bitmap)
            showDummyPrediction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        // 🔘 Satu tombol saja untuk ambil foto
        b.btnCapture.setOnClickListener {
            checkCameraPermissionAndOpen()
        }

        return b.root
    }

    // 🔒 Cek izin kamera sebelum buka kamera
    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionWarning()
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // 📸 Fungsi buka kamera (gabungan final)
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    // ⚠️ Jika user menolak izin sebelumnya
    private fun showPermissionWarning() {
        AlertDialog.Builder(requireContext())
            .setTitle("Izin Kamera Diperlukan")
            .setMessage("Aplikasi memerlukan akses kamera untuk mengambil foto makanan. Izinkan sekarang?")
            .setPositiveButton("Izinkan") { _, _ ->
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // 🚫 Jika izin ditolak permanen
    private fun showDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Izin Ditolak")
            .setMessage("Kamera tidak dapat digunakan tanpa izin. Aktifkan izin di pengaturan aplikasi.")
            .setPositiveButton("Buka Pengaturan") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", requireContext().packageName, null)
                )
                startActivity(intent)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // 🍱 Dummy hasil prediksi
    private fun showDummyPrediction() {
        val calories = Random.nextInt(200, 800)
        val carbs = Random.nextInt(20, 100)
        val protein = Random.nextInt(10, 50)
        val fat = Random.nextInt(5, 30)

        b.progressBar.visibility = View.VISIBLE
        b.resultContainer.visibility = View.GONE

        b.progressBar.postDelayed({
            b.progressBar.visibility = View.GONE
            b.resultContainer.visibility = View.VISIBLE

            b.tvCalories.text = "Kalori: $calories kcal"
            b.tvCarbs.text = "Karbohidrat: $carbs g"
            b.tvProtein.text = "Protein: $protein g"
            b.tvFat.text = "Lemak: $fat g"

            fadeIn(b.resultContainer)
        }, 1200)
    }

    private fun fadeIn(view: View) {
        val fade = AlphaAnimation(0f, 1f)
        fade.duration = 700
        view.startAnimation(fade)
        view.alpha = 1f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
