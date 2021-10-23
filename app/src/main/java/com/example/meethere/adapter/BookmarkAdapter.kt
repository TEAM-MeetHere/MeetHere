package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.BookmarkItem
import com.example.meethere.R

class BookmarkAdapter(val locationList: ArrayList<BookmarkItem>) :
    RecyclerView.Adapter<BookmarkAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_single_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos: Int = adapterPosition
                val tempItem: BookmarkItem = locationList.get(curPos)
                Toast.makeText(
                    parent.context,
                    "${tempItem.destination}에 대한 내용을 가져올 예정",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemLogo.setImageResource(locationList.get(position).item_star)
        holder.itemLocation.text = locationList.get(position).destination
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemLogo = itemView.findViewById<ImageView>(R.id.item_logo)
        val itemLocation = itemView.findViewById<TextView>(R.id.item_location)
    }
}