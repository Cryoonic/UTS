package com.example.uts

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityScanBinding
import com.example.uts.model.Food
import java.text.SimpleDateFormat
import java.util.*

class ScanActivity : AppCompatActivity() {
    private lateinit var b: ActivityScanBinding
    private var lastBitmap: Bitmap? = null

    private val takePicLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) {
            lastBitmap = bmp
            b.ivPreview.setImageBitmap(bmp)
            b.tvStatus.text = "Foto siap. Tekan Proses."
            b.btnProcess.isEnabled = true
        } else {
            Toast.makeText(this, "Gagal ambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) takePicLauncher.launch(null)
        else Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityScanBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnCapture.setOnClickListener { requestCamera.launch(Manifest.permission.CAMERA) }

        b.btnProcess.setOnClickListener {
            val bmp = lastBitmap
            if (bmp == null) { Toast.makeText(this, "Ambil foto dulu", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            b.progressBar.visibility = View.VISIBLE
            b.tvStatus.text = "Memproses..."
            b.root.postDelayed({
                b.progressBar.visibility = View.GONE
                // Dummy predictions
                val preds = listOf(
                    Food("Nasi Goreng", "Nasi goreng komplit", 350, 44.0, 12.5, 11.0, R.drawable.nasi_goreng),
                    Food("Salad Buah", "Segar dan ringan", 180, 30.0, 3.0, 4.0, R.drawable.salad_buah),
                    Food("Sate Ayam", "Sate dengan bumbu", 420, 8.0, 30.0, 22.0, R.drawable.sate_ayam)
                )
                val pick = preds.random()
                val intent = android.content.Intent(this, ScanResultActivity::class.java)
                intent.putExtra("food_name", pick.name)
                intent.putExtra("food_desc", pick.description)
                intent.putExtra("food_calories", pick.calories)
                intent.putExtra("food_carbs", pick.carbs)
                intent.putExtra("food_protein", pick.protein)
                intent.putExtra("food_fat", pick.fat)
                startActivity(intent)
            }, 800)
        }
    }

    private fun dateNow(): String = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
}
