package com.example.meethere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.databinding.ItemResultBinding
import kotlinx.android.synthetic.main.item_result.view.*
import android.widget.Toast

import android.R
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import java.security.AccessController.getContext


class ResultAdopter (
    private val results: MutableList<Result>
) : RecyclerView.Adapter<ResultAdopter.ResultViewHolder>() {
    class ResultViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder (binding.root) {
        val btn = binding.btnDetail

        init {
/*            binding.btnDetail.setOnClickListener {
                fun OnClick(view: View) {
                    Toast.makeText(view.context, "Item is clicked", Toast.LENGTH_SHORT).show()
                }
            }*/
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        /*return ResultViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_result,
                parent,
                false
            )
        )*/
        return ResultViewHolder(binding)
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

        holder.btn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
/*                Log.d("sss","Test btn${results[position].Name}")
                if(results.isNotEmpty())
                    results.remove(results[position])
                notifyDataSetChanged()*/
                val context=holder.itemView.context
                val intent = Intent(context, ShowDetail_2_8::class.java)
                intent.putExtra("Name",curAddress.Name)
                context.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return results.size
    }


}