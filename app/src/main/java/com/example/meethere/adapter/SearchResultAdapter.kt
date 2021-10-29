package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.AddressObject
import com.example.meethere.R
import com.example.meethere.SearchResultItem

class SearchResultAdapter(val searchResults: ArrayList<SearchResultItem>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

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

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                itemClickListener.onClick(addressObject)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
    }

    interface OnItemClickListener {
        fun onClick(addressObject: AddressObject)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun addSearchResults(searchResult: SearchResultItem) {
        searchResults.add(searchResult)
        notifyItemInserted(searchResults.size - 1)
    }

    private lateinit var itemClickListener: OnItemClickListener
}