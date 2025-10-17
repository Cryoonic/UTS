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
    private var foodList: List<Food>,   // Daftar data makanan
    private val onClick: (Food) -> Unit // Aksi saat item diklik
) : RecyclerView.Adapter<FoodGridAdapter.FoodViewHolder>() {

    // ViewHolder menyimpan referensi ke elemen tampilan setiap item grid
    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMore)
    }

    // Membuat tampilan baru (inflate layout) untuk item grid
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_grid, parent, false)
        return FoodViewHolder(view)
    }

    // Mengikat data makanan ke setiap elemen tampilan di grid
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.tvFoodName.text = food.name
        holder.imgFood.setImageResource(food.imageResId)


        // Klik pada tombol atau item akan memicu aksi yang sama
        holder.btnMore.setOnClickListener { onClick(food) }
        holder.itemView.setOnClickListener { onClick(food) }
    }

    // Memperbarui data dan merefresh RecyclerView
    fun updateList(newList: List<Food>){
        foodList = newList
        notifyDataSetChanged()
    }

    // Mengembalikan jumlah total item dalam grid
    override fun getItemCount(): Int = foodList.size

}