package com.example.uts.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uts.ApiClient
import com.example.uts.FoodDetailActivity
import com.example.uts.adapters.FoodGridAdapter
import com.example.uts.databinding.FragmentHomeBinding
import com.example.uts.model.Food
import com.example.uts.R
import com.example.uts.database.AppDatabase
import com.example.uts.model.FoodHistory
import com.example.uts.model.User
import com.example.uts.utils.SharedPref
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: SharedPref
    private lateinit var historyAdapter: FoodGridAdapter
    private var historyList: MutableList<Food> = mutableListOf()

    private var activeDate: Long = 0L

    private var activeFilter: String = "all"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())

        setupHeader()
        setupCalendarButton()
        setupWeeklyCalendar()
        setupHistoryFood()

        val today = normalizeDate(System.currentTimeMillis())
        activeDate = today
        loadDailySummary(today)
        loadHistoryFoodByDate(today)
        setupMealFilterSpinner()

        binding.btnAddFood.setOnClickListener {
            showAddFoodDialog()
        }


        return binding.root
    }

    // -------------------------------
    //  BAGIAN HEADER (USERNAME + KALENDER)
    // -------------------------------
    private fun setupHeader() {
        val username = pref.getSession() ?: "User"
        binding.tvHiUser.text = username
    }

    private fun setupCalendarButton() {
        binding.btnCalendar.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build()

            datePicker.show(parentFragmentManager, "datePicker")

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                // TODO: nanti dipakai untuk filter history & daily summary
            }
        }
    }

    private fun normalizeDate(timeMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }


    // -------------------------------
    //  Progrees bar
    // -------------------------------
    private fun loadDailySummary(selectedDate: Long){
        val username = pref.getSession() ?: return

        lifecycleScope.launch(Dispatchers.IO){
            val db = AppDatabase.getDatabase(requireContext())
            val user = db.userDao().getUser(username)
            var history = db.foodHistoryDao().getByDate(username, selectedDate)

            if (activeFilter != "all") {
                history = history.filter { it.mealType == activeFilter }
            }

            val totalCalories = history.sumOf { it.calories }
            val totalCarbs = history.sumOf { it.carbs }
            val totalProtein = history.sumOf { it.protein }
            val totalFat = history.sumOf { it.fat }
            val totalFiber = history.sumOf { it.fiber }

            val breakfastCal = history.filter { it.mealType == "breakfast" }.sumOf { it.calories }
            val lunchCal = history.filter { it.mealType == "lunch" }.sumOf { it.calories }
            val dinnerCal = history.filter { it.mealType == "dinner"}.sumOf { it.calories }
            val snackCal = history.filter { it.mealType == "snack" }.sumOf { it.calories }

            if (user == null) return@launch
            val tdee = calculateTDEE(user)


            withContext(Dispatchers.Main){
                renderSummaryUI(
                    breakfastCal, lunchCal, dinnerCal, snackCal,
                    totalCalories,totalCarbs, totalProtein, totalFat, totalFiber, tdee
                )
            }
        }
    }

    private fun calculateTDEE(user: User): Int{
        val bmr = if (user.gender == "male"){
            88.362 + (13.397 *user.weight!!) + (4.799 * user.height!!) - (5.677 * user.age!!)
        } else {
            447.593 + (9.247 * user.weight!!) + (3.098 * user.height!!) - (4.330 * user.age!!)
        }

        val factor = when (user.activityLevel){
            "Sedentary" -> 1.2
            "Light Activity" -> 1.55
            "Moderate Activity" -> 1.725
            else -> 1.2
        }

        return (bmr * factor).toInt()
    }

    private fun renderSummaryUI(
        breakfast: Int,
        lunch: Int,
        dinner: Int,
        snack: Int,
        totalCal: Int,
        carbs: Double,
        protein: Double,
        fat: Double,
        fiber: Double,
        tdee: Int
    ) {
        binding.tvBreakfastCal.text = "Breakfast \n$breakfast kcal"
        binding.tvLunchCal.text = "Lunch \n$lunch kcal"
        binding.tvDinnerCal.text = "Dinner \n$dinner kcal"
        binding.tvSnackCal.text = "Snack \n$snack kcal"

        animateCaloriesText(0, totalCal)

        val progress = ((totalCal.toFloat() / tdee.toFloat()) * 100f).coerceAtMost(100f)
        animateCaloriesProgress(progress.toInt())

        when {
            totalCal >= tdee -> {
                binding.progressCalories.setIndicatorColor(
                    resources.getColor(android.R.color.holo_red_dark, null)
                )
            }
            totalCal >= (tdee * 0.9) -> {
                binding.progressCalories.setIndicatorColor(
                    resources.getColor(android.R.color.holo_orange_dark, null)
                )
            }
            else -> {
                binding.progressCalories.setIndicatorColor(
                    resources.getColor(android.R.color.holo_green_dark, null)
                )
            }
        }


        binding.tvCarbs.text   = "Carbs \n${"%.1f".format(carbs)}g"
        binding.tvProtein.text = "Protein \n${"%.1f".format(protein)}g"
        binding.tvFat.text     = "Fat \n${"%.1f".format(fat)}g"
        binding.tvFiber.text   = "Fiber \n${"%.1f".format(fiber)}g"

    }
    private fun setupMealFilterSpinner() {
        val filterList = listOf("all", "breakfast", "lunch", "dinner", "snack")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filterList
        )

        binding.spFilterMeal.adapter = adapter

        binding.spFilterMeal.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    activeFilter = filterList[position]
                    loadDailySummary(activeDate)
                    loadHistoryFoodByDate(activeDate)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }



    private fun setupWeeklyCalendar() {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        val dayLayouts = listOf(
            binding.day1.root,
            binding.day2.root,
            binding.day3.root,
            binding.day4.root,
            binding.day5.root,
            binding.day6.root,
            binding.day7.root
        )

        val dayNames = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

        // ✅ SIMPAN TANGGAL SETIAP HARI DI LIST
        val dateList = mutableListOf<Long>()

        for (i in 0 until 7) {
            val dayView = dayLayouts[i]
            val tvName = dayView.findViewById<TextView>(R.id.tvDayName)
            val tvNum = dayView.findViewById<TextView>(R.id.tvDayNumber)

            tvName.text = dayNames[i]
            tvNum.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

            // ✅ SIMPAN TANGGAL ASLI
            dateList.add(calendar.timeInMillis)

            val today = Calendar.getInstance()
            val isToday =
                calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

            dayView.setBackgroundResource(
                if (isToday) R.drawable.bg_option_selected else R.drawable.bg_option_unselected
            )

            dayView.setOnClickListener {
                highlightSelectedDay(i, dayLayouts)

                activeDate = normalizeDate(dateList[i])
                loadDailySummary(activeDate)
                loadHistoryFoodByDate(activeDate)
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // ✅ AUTO LOAD HARI INI PERTAMA KALI
        val todayIndex = dateList.indexOfFirst {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it
            val now = Calendar.getInstance()

            cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
        }

        if (todayIndex != -1) {
            highlightSelectedDay(todayIndex, dayLayouts)
            activeDate = normalizeDate(dateList[todayIndex])
            loadDailySummary(activeDate)
            loadHistoryFoodByDate(activeDate)
        }
    }


    private fun highlightSelectedDay(selectedIndex: Int, dayLayouts: List<View>) {
        dayLayouts.forEachIndexed { index, view ->
            view.setBackgroundResource(
                if (index == selectedIndex)
                    R.drawable.bg_option_selected
                else
                    R.drawable.bg_option_unselected
            )
        }
    }

    private fun setupHistoryFood() {
        historyAdapter = FoodGridAdapter(emptyList()) { food ->
            val i = Intent(requireContext(), FoodDetailActivity::class.java)
            i.putExtra("food_name", food.foodname)
            i.putExtra("food_calories", food.calories)
            i.putExtra("food_carbs", food.carbs)
            i.putExtra("food_protein", food.protein)
            i.putExtra("food_fat", food.fat)
            i.putExtra("food_fiber", food.fiber)
            i.putExtra("meal_type", food.mealType)
            startActivity(i)
        }


        binding.recyclerHistory.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun loadHistoryFoodByDate(selectedDate: Long) {
        val username = pref.getSession() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            var history = db.foodHistoryDao().getByDate(username, selectedDate)

            if (activeFilter != "all"){
                history = history.filter { it.mealType == activeFilter }
            }


            withContext(Dispatchers.Main) {
                historyAdapter.updateList(history)
            }
        }
    }
    private fun showAddFoodDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_food, null)

        val etFood = dialogView.findViewById<EditText>(R.id.etFoodName)
        val etGram = dialogView.findViewById<EditText>(R.id.etGram)
        val spMeal = dialogView.findViewById<Spinner>(R.id.spMealType)

        val mealList = listOf("breakfast", "lunch", "dinner", "snack")
        spMeal.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mealList
        )

        AlertDialog.Builder(requireContext())
            .setTitle("")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val foodName = etFood.text.toString()
                val gram = etGram.text.toString().toDoubleOrNull() ?: 0.0
                val mealType = spMeal.selectedItem.toString()

                if (foodName.isNotEmpty() && gram > 0) {
                    fetchFoodFromApi(foodName, gram, mealType)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun fetchFoodFromApi(foodName: String, gram: Double, mealType: String) {

        lifecycleScope.launch {
            try {
                val response = ApiClient.api.searchFood(
                    query = foodName,
                    apiKey = "djVoNuFPiKOBfeokRC4KXxJ65QfPjNz2DvydbqJq"
                )

                val food = response.foods.first()

                var calories = 0.0
                var carbs = 0.0
                var protein = 0.0
                var fat = 0.0
                var fiber = 0.0

                food.foodNutrients.forEach {
                    when (it.nutrientName.lowercase()) {
                        "energy" -> calories = it.value
                        "carbohydrate, by difference" -> carbs = it.value
                        "protein" -> protein = it.value
                        "total lipid (fat)" -> fat = it.value
                        "fiber, total dietary" -> fiber = it.value
                    }
                }

                val multiplier = gram / 100

                val history = FoodHistory(
                    username = pref.getSession()!!,
                    mealType = mealType,
                    calories = (calories * multiplier).toInt(),
                    carbs = carbs * multiplier,
                    protein = protein * multiplier,
                    fat = fat * multiplier,
                    fiber = fiber * multiplier,
                    foodname = foodName,
                    timestamp = normalizeDate(System.currentTimeMillis())
                )

                val db = AppDatabase.getDatabase(requireContext())
                db.foodHistoryDao().insert(history)

                loadDailySummary(activeDate)
                loadHistoryFoodByDate(activeDate)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Food not found", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun animateCaloriesProgress(targetProgress: Int) {
        val animator = android.animation.ValueAnimator.ofInt(
            binding.progressCalories.progress,
            targetProgress
        )

        animator.duration = 800
        animator.addUpdateListener { animation ->
            binding.progressCalories.progress = animation.animatedValue as Int
        }

        animator.start()
    }

    private fun animateCaloriesText(start: Int, end: Int) {
        val animator = android.animation.ValueAnimator.ofInt(start, end)
        animator.duration = 800
        animator.addUpdateListener {
            binding.tvTotalCalories.text = "${it.animatedValue} kcal"
        }
        animator.start()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
