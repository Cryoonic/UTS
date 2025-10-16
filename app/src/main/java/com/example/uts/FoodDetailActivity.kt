package com.example.uts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.databinding.ActivityFoodDetailBinding

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityFoodDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val name = intent.getStringExtra("food_name") ?: "Unknown"
        val desc = intent.getStringExtra("food_desc") ?: ""
        val calories = intent.getIntExtra("food_calories", 0)
        val carbs = intent.extras?.getDouble("food_carbs") ?: 0.0
        val protein = intent.extras?.getDouble("food_protein") ?: 0.0
        val fat = intent.extras?.getDouble("food_fat") ?: 0.0
        val image = intent.getIntExtra("food_image", 0)

        b.tvName.text = name
        b.tvDescription.text = desc
        b.tvCalories.text = "${calories} kcal"
        b.tvProtein.text = "Protein: ${protein}g"
        b.tvCarbs.text = "Carbs: ${carbs}g"
        b.tvFat.text = "Fat: ${fat}g"
        if (image != 0) b.ivFood.setImageResource(image)
    }
}
