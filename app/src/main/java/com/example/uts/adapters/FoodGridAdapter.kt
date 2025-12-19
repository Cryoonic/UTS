package com.example.uts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.model.Food
import com.example.uts.model.FoodHistory

class FoodGridAdapter(
    private var foodList: List<FoodHistory>,
    private val onClick: (FoodHistory) -> Unit
) : RecyclerView.Adapter<FoodGridAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
        val tvProtein: TextView = itemView.findViewById(R.id.tvProtein)
        val tvCarbs: TextView = itemView.findViewById(R.id.tvCarbs)
        val tvFat: TextView = itemView.findViewById(R.id.tvFat)
        val tvFiber: TextView = itemView.findViewById(R.id.tvFiber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_grid, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]

        holder.tvFoodName.text = food.foodname
        holder.tvCalories.text = "Calories: ${food.calories} kcal"
        holder.tvCarbs.text = "Carbs: ${"%.1f".format(food.carbs)} g"
        holder.tvProtein.text = "Protein: ${"%.1f".format(food.protein)} g"
        holder.tvFat.text = "Fat: ${"%.1f".format(food.fat)} g"
        holder.tvFiber.text = "Fiber: ${"%.1f".format(food.fiber)} g"

        holder.itemView.setOnClickListener { onClick(food) }
    }

    fun updateList(newList: List<FoodHistory>){
        foodList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = foodList.size
}
