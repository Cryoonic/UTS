package com.example.uts.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.uts.FoodDetailActivity
import com.example.uts.adapters.FoodGridAdapter
import com.example.uts.databinding.FragmentHomeBinding
import com.example.uts.model.Food
import com.example.uts.R
import com.example.uts.utils.SharedPref
import java.util.*
class HomeFragment : Fragment() {
   private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodGridAdapter: FoodGridAdapter
    private lateinit var pref: SharedPref
    private lateinit var fullFoodList: List<Food>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())

        setupGreeting()
        setupRecyclerView()
        setupSearchBar()

        return binding.root
    }

    private fun setupGreeting(){
        val user = pref.getUser()
        val username = user?.username ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when(hour){
            in 5..11 -> "Good morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        binding.tvHiUser.text = "Hi $username"
        binding.tvGreeting.text = greeting
    }

    private fun setupRecyclerView() {
        fullFoodList = listOf(
            Food("Nasi Goreng", "Nasi goreng klasik Indonesia yang dimasak dengan bumbu tradisional, potongan sayuran, dan telur atau daging pilihan. Gurih dan kaya rasa, sempurna untuk sarapan, makan siang, maupun makan malam. ", 450, 65.0, 12.5, 15.0, R.drawable.nasi_goreng),
            Food("Katsu Don", "Hidangan Jepang yang lezat, terdiri dari nasi hangat disajikan dengan daging ayam atau babi goreng tepung renyah, telur setengah matang, dan saus khas yang gurih. Pas untuk makan siang atau malam yang memuaskan.", 600, 75.0, 30.0, 25.0, R.drawable.katsu_don),
            Food("Bibimbap", "hidangan khas Korea yang sehat dan lezat, terdiri dari nasi hangat, sayuran segar, telur, dan daging pilihan, disajikan dengan saus gochujang pedas manis. Kombinasi bahan yang seimbang membuatnya nikmat dan mengenyangkan untuk makan siang atau malam.", 500, 70.0, 25.0, 15.0, R.drawable.bimbimbap),
            Food("Avocado Salad", "Avocado Salad segar dan sehat, terdiri dari potongan alpukat matang, sayuran hijau, dan dressing ringan. Cocok untuk menu sarapan, makan siang, atau camilan sehat yang kaya lemak sehat dan menyegarkan.", 250, 15.0, 7.0, 20.0, R.drawable.avocado_salad)
        )

        foodGridAdapter = FoodGridAdapter(fullFoodList) { food ->
            val intent = Intent(requireContext(), FoodDetailActivity::class.java)
            intent.putExtra("food_name", food.name)
            intent.putExtra("food_desc", food.description)
            intent.putExtra("food_calories", food.calories)
            intent.putExtra("food_carbs", food.carbs)
            intent.putExtra("food_protein", food.protein)
            intent.putExtra("food_fat", food.fat)
            intent.putExtra("food_image", food.imageResId)
            startActivity(intent)
        }

        binding.recyclerFood.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = foodGridAdapter
        }
    }

    private fun setupSearchBar(){
        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               val query = s.toString().trim().lowercase(Locale.ROOT)
                val filteredList = if (query.isEmpty()){
                    fullFoodList
                } else{
                    fullFoodList.filter{
                        it.name.lowercase(Locale.ROOT).contains(query)
                    }
                }
                foodGridAdapter.updateList(filteredList)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
