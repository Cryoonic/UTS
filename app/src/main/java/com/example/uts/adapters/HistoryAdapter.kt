package com.example.uts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.model.Food

class HistoryAdapter(
    private val foodList: List<Food>    // Daftar makanan yang disimpan dalam riwayat
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    // ViewHolder menyimpan referensi ke elemen tampilan di layout item_food.xml
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFoodname: TextView = view.findViewById(R.id.tvFoodName)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
    }

    // Membuat tampilan item baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    // Mengikat data setiap objek Food ke tampilan item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]
        holder.tvFoodname.text = food.name
        holder.tvCalories.text = "${food.calories} kalori"
    }

    // Mengembalikan jumlah total item dalam daftar riwayat
    override fun getItemCount(): Int = foodList.size
}
