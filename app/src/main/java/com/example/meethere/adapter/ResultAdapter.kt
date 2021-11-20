package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.databinding.ItemResultBinding
import kotlinx.android.synthetic.main.item_result.view.*

import android.content.Intent
import android.util.Log
import com.example.meethere.activity.OtherRouteActivity
import com.example.meethere.objects.ResultObject
import com.example.meethere.activity.ShowDetailActivity
import com.example.meethere.objects.ItemComponent


class ResultAdapter(
    private val resultObjects: MutableList<ResultObject>, private var routelists : MutableList<MutableList<ItemComponent>> = arrayListOf()
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    class ResultViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
        val btn = binding.btnDetail
        val btn_others = binding.btnOtherRoute

        init {
/*            binding.btnDetail.setOnClickListener {
                fun OnClick(view: View) {
                    Toast.makeText(view.context, "Item is clicked", Toast.LENGTH_SHORT).show()
                }
            }*/
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun addRouteList(routelist : MutableList<ItemComponent>){
        routelists.add(routelist)
        notifyItemInserted(routelists.size - 1)
    }

    fun addResult(resultObject: ResultObject) {
        resultObjects.add(resultObject)
        notifyItemInserted(resultObjects.size - 1)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val curAddress = resultObjects[position]
        val curPerson = routelists[position]
        holder.itemView.apply {
            textViewName.text = curAddress.Name
            if(curAddress.Time >= 60) textViewTime.setText(""+(curAddress.Time / 60)+"시간 "+ (curAddress.Time % 60)+"분 소요 예정")
            else textViewTime.setText(""+curAddress.Time+"분 소요 예정")
        }

        holder.btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
/*                Log.d("sss","Test btn${results[position].Name}")
                if(results.isNotEmpty())
                    results.remove(results[position])
                notifyDataSetChanged()*/
                val context = holder.itemView.context
                val intent = Intent(context, ShowDetailActivity::class.java)
                intent.putExtra("Name", curAddress.Name)
                context.startActivity(intent)
            }
        })

        holder.btn_others.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0 :View?){
                val context = holder.itemView.context
                val intent = Intent(context, OtherRouteActivity::class.java)
                Log.d("result adapter asdfasdf", "asdfasdf")
                intent.putExtra("rlist", ArrayList(curPerson))
                Log.d("result adapter curPerson", curPerson.toString())
                context.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return resultObjects.size
    }


}