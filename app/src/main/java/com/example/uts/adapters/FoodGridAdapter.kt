package com.example.uts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.model.Food

class FoodGridAdapter(
    private var foodList: List<Food>,
    private val onClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodGridAdapter.FoodViewHolder>() {
    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_grid, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.tvFoodName.text = food.name
        holder.imgFood.setImageResource(food.imageResId)

        holder.btnMore.setOnClickListener { onClick(food) }
        holder.itemView.setOnClickListener { onClick(food) }
    }

    fun updateList(newList: List<Food>){
        foodList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = foodList.size

}