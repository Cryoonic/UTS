package com.example.uts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.model.Food

class HistoryAdapter(
    private val foodList: List<Food>
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
        val food = foodList[position]
        holder.tvFoodname.text = food.name
        holder.tvCalories.text = "${food.calories} kalori"
    }

    override fun getItemCount(): Int = foodList.size
}
