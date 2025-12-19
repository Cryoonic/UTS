package com.example.uts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.model.Food
import com.example.uts.model.FoodHistory

class HistoryAdapter(
    private var historyList: List<FoodHistory>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFoodname: TextView = view.findViewById(R.id.tvFoodName)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = historyList[position]
        holder.tvFoodname.text = food.foodname
        holder.tvCalories.text = "${food.calories} kcal"
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newList: List<FoodHistory>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
