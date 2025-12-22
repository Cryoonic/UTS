package com.example.uts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts.databinding.FragmentHistoryBinding
import com.example.uts.utils.SharedPref
import com.example.uts.adapters.HistoryAdapter
import com.example.uts.model.FoodHistory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class HistoryFragment : Fragment() {

    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!
    private lateinit var pref: SharedPref
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _b = FragmentHistoryBinding.inflate(inflater, container, false)
        pref = SharedPref(requireContext())

        setupRecyclerView()
        loadHistoryFromDatabase()

        return b.root
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList())

        b.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        b.rvHistory.adapter = adapter
    }

    private fun loadHistoryFromDatabase() {
        val username = pref.getSession() ?: return

        // range hari ini (00:00 - 23:59)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis - 1

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("food_history")
            .whereEqualTo("username", username)
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .whereLessThanOrEqualTo("timestamp", endOfDay)
            .get()
            .addOnSuccessListener { snapshot ->

                val history = snapshot.toObjects(FoodHistory::class.java)

                adapter.updateData(history)

                val totalCalories = history.sumOf { it.calories }
                b.tvCaloriesGoal.text = "Total Hari Ini: $totalCalories kcal"
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

