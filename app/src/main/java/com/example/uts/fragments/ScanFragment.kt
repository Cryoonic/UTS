package com.example.uts.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.uts.databinding.FragmentScanBinding
import com.example.uts.viewmodel.api.MLApi
import java.io.File
import java.io.FileOutputStream


class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val api = MLApi()
    private var currentImageFile: File? = null
    private var photoUri: Uri? = null

    // Camera launcher
    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                binding.ivFoodPreview.setImageURI(uri)
                binding.btnAnalyze.isEnabled = true
                currentImageFile = File(uri.path!!)
            }
        }
    }

    // Gallery launcher
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.ivFoodPreview.setImageURI(it)
            binding.btnAnalyze.isEnabled = true
            currentImageFile = uriToFile(it)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check API health
        checkApiConnection()

        // Setup button listeners
        binding.btnCamera.setOnClickListener { requestCamera() }
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnAnalyze.setOnClickListener { analyzeFood() }
    }

    private fun checkApiConnection() {
        api.checkHealth { isHealthy ->
            activity?.runOnUiThread {
                if (isHealthy) {
                    Toast.makeText(context, "✅ API Connected", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "❌ API Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun requestCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().cacheDir
        )

        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        takePicture.launch(photoUri)
    }


    private fun openGallery() {
        pickImage.launch("image/*")
    }

    private fun analyzeFood() {
        currentImageFile?.let { file ->
            binding.progressBar.visibility = View.VISIBLE
            binding.cardResult.visibility = View.GONE
            binding.btnAnalyze.isEnabled = false

            api.predictFood(file) { result ->
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAnalyze.isEnabled = true

                    if (result?.success == true) {
                        displayResult(result)
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${result?.error ?: "Unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } ?: run {
            Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayResult(result: MLApi.PredictionResult) {
        binding.cardResult.visibility = View.VISIBLE

        binding.tvFoodName.text = result.prediction?.replaceFirstChar { it.uppercase() } ?: "Unknown"

        result.confidence?.let {
            binding.tvConfidence.text = "Confidence: ${(it * 100).toInt()}%"
        } ?: run {
            binding.tvConfidence.text = "Confidence: N/A"
        }

        result.nutrition?.let { nutrition ->
            binding.tvCalories.text = "Calories: ${nutrition.calories} kcal"
            binding.tvProtein.text = "Protein: ${nutrition.protein}g"
            binding.tvCarbs.text = "Carbs: ${nutrition.carbs}g"
            binding.tvFat.text = "Fat: ${nutrition.fat}g"
            binding.tvServing.text = "Serving: ${nutrition.serving_size}"
        }
    }
    private fun uriToFile(uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        val file = File.createTempFile("IMG_", ".jpg", requireContext().cacheDir)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
}