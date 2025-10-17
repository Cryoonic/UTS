package com.example.uts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts.FoodDetailActivity
import com.example.uts.adapters.FoodAdapter
import com.example.uts.databinding.FragmentHistoryBinding
import com.example.uts.model.Food
import com.example.uts.utils.SharedPref
import com.example.uts.R

class HistoryFragment : Fragment() {
    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!
    private lateinit var pref: SharedPref
    private lateinit var fullFoodList: List<Food>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi View Binding dan SharedPref
        _b = FragmentHistoryBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())

        // Set tampilan awal untuk goal kalori dan daftar makanan
        setupCaloriesGoal()
        setupRecyclerView()

        return b.root
    }

    private fun setupCaloriesGoal() {
        // Dummy progress
        b.tvCaloriesGoal.text = "Calories Goal: 2000 kcal"
        b.progressCalories.progress = 50
        b.tvCaloriesPercent.text = "50% tercapai"
    }

    private fun setupRecyclerView() {
        // Dummy food list
        fullFoodList = listOf(
            Food("Nasi Goreng", "Nasi goreng klasik Indonesia yang dimasak dengan bumbu tradisional, potongan sayuran, dan telur atau daging pilihan. Gurih dan kaya rasa, sempurna untuk sarapan, makan siang, maupun makan malam. ", 450, 65.0, 12.5, 15.0, R.drawable.nasi_goreng),
            Food("Katsu Don", "Hidangan Jepang yang lezat, terdiri dari nasi hangat disajikan dengan daging ayam atau babi goreng tepung renyah, telur setengah matang, dan saus khas yang gurih. Pas untuk makan siang atau malam yang memuaskan.", 600, 75.0, 30.0, 25.0, R.drawable.katsu_don),
            Food("Bibimbap", "hidangan khas Korea yang sehat dan lezat, terdiri dari nasi hangat, sayuran segar, telur, dan daging pilihan, disajikan dengan saus gochujang pedas manis. Kombinasi bahan yang seimbang membuatnya nikmat dan mengenyangkan untuk makan siang atau malam.", 500, 70.0, 25.0, 15.0, R.drawable.bimbimbap),
            Food("Avocado Salad", "Avocado Salad segar dan sehat, terdiri dari potongan alpukat matang, sayuran hijau, dan dressing ringan. Cocok untuk menu sarapan, makan siang, atau camilan sehat yang kaya lemak sehat dan menyegarkan.", 250, 15.0, 7.0, 20.0, R.drawable.avocado_salad)
        )

        // Mengatur RecyclerView untuk menampilkan daftar makanan
        b.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        b.rvHistory.adapter = FoodAdapter(fullFoodList) { food ->
            // Aksi ketika item diklik → membuka halaman detail makanan
            val i = android.content.Intent(requireContext(), FoodDetailActivity::class.java)
            i.putExtra("food_name", food.name)
            i.putExtra("food_desc", food.description)
            i.putExtra("food_calories", food.calories)
            i.putExtra("food_carbs", food.carbs)
            i.putExtra("food_protein", food.protein)
            i.putExtra("food_fat", food.fat)
            i.putExtra("food_image", food.imageResId)
            startActivity(i)
        }
    }

    override fun onDestroyView() {
        // Membersihkan binding agar tidak terjadi memory leak
        super.onDestroyView()
        _b = null
    }
}
