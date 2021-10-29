package com.example.meethere.adapter

import android.app.appsearch.SearchResult
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.Address
import com.example.meethere.R
import com.example.meethere.SearchResultItem
import com.example.meethere.activity.SelectDestination_2_6Activity
import com.example.meethere.activity.ShowDetail_2_8Activity
import com.example.meethere.activity.ShowResult_2_7Activity

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
        holder.name.text = searchResults[position].name
        holder.road.text = searchResults[position].road
        holder.address.text = searchResults[position].address
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener (object : View.OnClickListener {
            override fun onClick(p0: View?) {
                itemClickListener.onClick(searchResults[position].name, searchResults[position].road)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
    }

    interface OnItemClickListener {
        fun onClick(addressName: String, addressRoad: String)
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