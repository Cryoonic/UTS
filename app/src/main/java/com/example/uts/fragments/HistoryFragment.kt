package com.example.uts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts.databinding.FragmentHistoryBinding
import com.example.uts.utils.SharedPref
import com.example.uts.adapters.HistoryAdapter
import com.example.uts.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val today = System.currentTimeMillis() // gunakan timestamp hari ini

        CoroutineScope(Dispatchers.IO).launch {
            val history = AppDatabase.getDatabase(requireContext())
                .foodHistoryDao()
                .getByDate(username, today)  // pakai getByDate, bukan getAllHistory

            withContext(Dispatchers.Main) {
                adapter.updateData(history)

                // hitung total kalori
                val totalCalories = history.sumOf { it.calories }
                b.tvCaloriesGoal.text = "Total Hari Ini: $totalCalories kcal"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

