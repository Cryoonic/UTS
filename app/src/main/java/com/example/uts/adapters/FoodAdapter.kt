package com.example.uts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.model.Food

class FoodAdapter(
    private var foodList: List<Food>,
    private val onClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivFood: ImageView = view.findViewById(R.id.imgFood)
        val tvName: TextView = view.findViewById(R.id.tvFoodName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = foodList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val f = foodList[position]
        holder.ivFood.setImageResource(if (f.imageResId != 0) f.imageResId else R.mipmap.ic_launcher)
        holder.tvName.text = f.name
        holder.itemView.setOnClickListener { onClick(f) }
    }

    fun updateList(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
