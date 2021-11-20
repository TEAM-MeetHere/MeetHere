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
import com.example.meethere.objects.RouteItemComponent
import com.example.meethere.objects.TimeWalkFee


class ResultAdapter(
    private val resultObjects: MutableList<ResultObject>, private val everyDetailRouteLists : MutableList<MutableList<MutableList<RouteItemComponent>>> = arrayListOf(),
    private val wholeRouteLists : MutableList<MutableList<ItemComponent>>, private val min_array : MutableList<Int>
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    class ResultViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
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


    fun addMin(minelem : Int){
        min_array.add(minelem)
        notifyItemInserted(min_array.size-1)
    }

    fun addWholeRouteList(wholeRouteList : MutableList<ItemComponent>){
        wholeRouteLists.add(wholeRouteList)
        notifyItemInserted(wholeRouteLists.size-1)
    }

    fun addDetailList(detailRouteList : MutableList<MutableList<RouteItemComponent>>){
        everyDetailRouteLists.add(detailRouteList)
        notifyItemInserted(everyDetailRouteLists.size-1)
    }


    fun addResult(resultObject: ResultObject) {
        resultObjects.add(resultObject)
        notifyItemInserted(resultObjects.size - 1)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val curMin = min_array[position]
        val curAddress = resultObjects[position]
        val curPerson = everyDetailRouteLists[position]
        val curPersonWholeRoute = wholeRouteLists[position]
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
                notifyDataSetChanged()*
 */
                val context = holder.itemView.context
                val intent = Intent(context, ShowDetailActivity::class.java)
                Log.d("이 새끼의 최소 인덱스", curMin.toString())
                intent.putExtra("resultToDetailWholeRoute", ArrayList(curPersonWholeRoute))
                intent.putExtra("resultToDetailDetailRoute", ArrayList(curPerson))
                intent.putExtra("resultToDetailMinIndex", curMin)
                context.startActivity(intent)
            }
        })

//        holder.btn_others.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(p0 :View?){
//                val context = holder.itemView.context
//                val intent = Intent(context, OtherRouteActivity::class.java)
//                Log.d("result adapter asdfasdf", "asdfasdf")
//                intent.putExtra("rlist", ArrayList(curPerson))
//                Log.d("result adapter curPerson", curPerson.toString())
//                context.startActivity(intent)
//            }
//        })
    }

    override fun getItemCount(): Int {
        return resultObjects.size
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

}