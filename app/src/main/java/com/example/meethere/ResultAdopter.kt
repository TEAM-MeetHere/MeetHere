package com.example.meethere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_result.view.*

class ResultAdopter (
    private val results: MutableList<Result>
) : RecyclerView.Adapter<ResultAdopter.ResultViewHolder>() {
    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_result,
                parent,
                false
            )
        )
    }

    /*   private fun toggleStrikeThrough(textViewAddress: TextView, textViewName: TextView) {

       }*/
    fun addResult(result: Result) {
        results.add(result)
        notifyItemInserted(results.size - 1)
    }

    override fun onBindViewHolder(holder: ResultAdopter.ResultViewHolder, position: Int) {
        val curAddress = results[position]
        holder.itemView.apply {
            textViewName.text = curAddress.Name
            textViewTime.text = curAddress.Time.toString()
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }
}