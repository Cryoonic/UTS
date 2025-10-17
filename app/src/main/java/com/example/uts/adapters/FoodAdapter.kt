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
    private var foodList: List<Food>,   // Data list makanan
    private val onClick: (Food) -> Unit     // Aksi ketika item diklik
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    // ViewHolder untuk mereferensikan elemen-elemen dalam setiap item tampilan
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivFood: ImageView = view.findViewById(R.id.imgFood)
        val tvName: TextView = view.findViewById(R.id.tvFoodName)

    }

    // Membuat tampilan baru untuk item ketika diperlukan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(v)
    }

    // Mengembalikan jumlah total item dalam daftar
    override fun getItemCount() = foodList.size

    // Menghubungkan data Food dengan tampilan di posisi tertentu
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val f = foodList[position]
        holder.ivFood.setImageResource(if (f.imageResId != 0) f.imageResId else R.mipmap.ic_launcher)
        holder.tvName.text = f.name
        holder.itemView.setOnClickListener { onClick(f) }
    }

    // Memperbarui daftar makanan dan me-refresh tampilan RecyclerView
    fun updateList(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
