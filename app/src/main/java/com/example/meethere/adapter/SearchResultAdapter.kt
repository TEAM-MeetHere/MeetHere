package com.example.meethere.adapter

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.AddressObject
import com.example.meethere.R
import com.example.meethere.SearchResultItem
import com.example.meethere.activity.ShowDetail_2_8Activity

class SearchResultAdapter(val searchResults: ArrayList<SearchResultItem>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var oldPosition = -1

    private var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_searchresult, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: SearchResultAdapter.ViewHolder, position: Int) {
        holder.name.text = searchResults[holder.adapterPosition].name
        holder.road.text = searchResults[holder.adapterPosition].road
        holder.address.text = searchResults[holder.adapterPosition].address

        var addressObject = AddressObject(
            searchResults[holder.adapterPosition].name,
            "",
            searchResults[holder.adapterPosition].road,
            searchResults[holder.adapterPosition].address,
            searchResults[holder.adapterPosition].x,
            searchResults[holder.adapterPosition].y
        )

        if (selectedPosition == holder.adapterPosition) holder.buttonSelect.visibility = View.VISIBLE
        else holder.buttonSelect.visibility = View.INVISIBLE

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                itemClickListener.onClick(addressObject, holder.adapterPosition)

                oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)

                holder.buttonSelect.visibility = View.VISIBLE
            }
        })

        holder.buttonSelect.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                itemClickListener2.onClick(addressObject, holder.adapterPosition)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
        val buttonSelect: Button = itemView.findViewById(R.id.buttonSelect)
    }

    interface OnItemClickListener {
        fun onClick(addressObject: AddressObject, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun setItemClickListener2(onItemClickListener: OnItemClickListener) {
        this.itemClickListener2 = onItemClickListener
    }

    fun addSearchResults(searchResult: SearchResultItem) {
        searchResults.add(searchResult)
        notifyItemInserted(searchResults.size - 1)
    }

    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var itemClickListener2: OnItemClickListener

    override fun getItemViewType(position: Int): Int {
        return position
    }
}