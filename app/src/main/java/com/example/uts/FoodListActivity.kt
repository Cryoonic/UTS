package com.example.uts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts.databinding.ActivityFoodListBinding
import com.example.uts.adapters.FoodAdapter
import com.example.uts.model.Food

class FoodListActivity : AppCompatActivity() {
    // View binding untuk mengakses komponen layout secara langsung
    private lateinit var binding: ActivityFoodListBinding
    // Adapter untuk menampilkan daftar makanan dalam RecyclerView
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dummy data
        val foodList = listOf(
            Food("Nasi Goreng", "Nasi goreng dengan telur dan ayam", 350,10.0,10.0,10.0,R.drawable.nasi_goreng),
            Food("Salad Buah", "Salad segar dengan dressing madu", 180, 10.0,10.0,10.0,R.drawable.salad_buah),
            Food("Sate Ayam", "Sate ayam dengan bumbu kacang", 420, 10.0,10.0, 10.0,R.drawable.sate_ayam),
            Food("Sop Sayur", "Sup sehat dengan sayuran segar", 150,10.0,10.0,10.0, R.drawable.sop_sayur)
        )

        // Inisialisasi adapter dengan listener klik item
        foodAdapter = FoodAdapter(foodList) { food ->
            // Saat item diklik, pindah ke halaman detail makanan
            val intent = Intent(this, FoodDetailActivity::class.java)
            intent.putExtra("food_name", food.name)
            intent.putExtra("food_desc", food.description)
            intent.putExtra("food_calories", food.calories)
            intent.putExtra("food_image", food.imageResId)
            startActivity(intent)
        }

        // Mengatur RecyclerView agar menampilkan daftar makanan secara vertikal
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = foodAdapter
    }
}
