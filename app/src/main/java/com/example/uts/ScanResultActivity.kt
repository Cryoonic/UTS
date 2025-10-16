package com.example.uts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityScanResultBinding
import com.example.uts.model.Food
import com.example.uts.utils.SharedPref
import java.text.SimpleDateFormat
import java.util.*

class ScanResultActivity : AppCompatActivity() {
    private lateinit var b: ActivityScanResultBinding
    private lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(b.root)
        pref = SharedPref(this)

        val name = intent.getStringExtra("food_name") ?: "Unknown"
        val desc = intent.getStringExtra("food_desc") ?: ""
        val calories = intent.getIntExtra("food_calories", 0)
        val carbs = intent.extras?.getDouble("food_carbs") ?: 0.0
        val protein = intent.extras?.getDouble("food_protein") ?: 0.0
        val fat = intent.extras?.getDouble("food_fat") ?: 0.0

        b.tvName.text = name
        b.tvCalories.text = "$calories kkal"
        b.tvMacros.text = "Carbs: ${carbs}g  Protein: ${protein}g  Fat: ${fat}g"
        b.tvDescription.text = desc

        b.btnSave.setOnClickListener {
            val item = Food(name, desc, calories, carbs, protein, fat, R.drawable.default_food)
            pref.addHistoryItem(item)
            Toast.makeText(this, "Disimpan ke riwayat", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun dateNow(): String = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
}
