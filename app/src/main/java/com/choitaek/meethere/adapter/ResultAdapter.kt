package com.choitaek.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.choitaek.meethere.databinding.ItemResultBinding
import kotlinx.android.synthetic.main.item_result.view.*

import android.content.Intent
import android.util.Log
import com.choitaek.meethere.activity.ShowDetailActivity
import com.choitaek.meethere.activity.ShowDetailCityToCityActivity
import com.choitaek.meethere.objects.*


class ResultAdapter(
    private val resultObjects: MutableList<ResultObject>, private val everyDetailRouteLists : MutableList<MutableList<MutableList<RouteItemComponent>>> = arrayListOf(),
    private val wholeRouteLists : MutableList<MutableList<ItemComponent>>, private val min_array : MutableList<Int>, private val wholeCityToCity : MutableList<MutableList<RouteCityToCity>>,
    private val wholelonlat : MutableList<SourceDestination>
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

    fun addCityToCity(citytocityElem : MutableList<RouteCityToCity>){
        wholeCityToCity.add(citytocityElem)
        Log.d("result -> 어뎁터(상세 경로 파트)", citytocityElem.toString())
        notifyItemInserted(wholeCityToCity.size-1)
    }

    fun addSourceDestination(soToDes : SourceDestination){
        wholelonlat.add(soToDes)
        Log.d("result -> 어뎁터(위도, 경도)", soToDes.toString())
        notifyItemInserted(wholelonlat.size-1)
    }

    fun addMin(minelem : Int){
        Log.d("어댑터 출현", "ㅇ")
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
        val curPersonCityToCity = wholeCityToCity[position]
        val curSourceToDes = wholelonlat[position]

        if(curPersonCityToCity.size == 0) {
            holder.itemView.apply {
                textViewName.text = curAddress.Name
                if (curAddress.Time >= 60) textViewTime.setText("" + (curAddress.Time / 60) + "시간 " + (curAddress.Time % 60) + "분 소요 예정")
                else textViewTime.setText("" + curAddress.Time + "분 소요 예정")
            }
        }
        else{
            // 시외 경로가 존재할 경우 시간 출력 화면에 시간이 아닌 시외 경로가 존재합니다라는 정보를 출력.
            holder.itemView.apply{
                textViewName.text = curAddress.Name
                textViewTime.setTextSize(2, 16f)
                textViewTime.setText("도시 간 이동수단이 존재합니다\n")
                textViewTime.append("상세 보기를 참조해주세요")
            }
        }

        holder.btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val context = holder.itemView.context
                if(curPersonCityToCity.size == 0) {
                    // 현재 사람이 시외 경로에 대한 데이터가 존재하지 않을 경우.
                    val intent = Intent(context, ShowDetailActivity::class.java)
                    Log.d("이 새끼의 최소 인덱스", curMin.toString())
                    intent.putExtra("resultToDetailWholeRoute", ArrayList(curPersonWholeRoute))
                    intent.putExtra("resultToDetailDetailRoute", ArrayList(curPerson))
                    intent.putExtra("resultToDetailMinIndex", curMin)
                    context.startActivity(intent)
                }
                else{
                    // 현재 position의 사람이 시외 경로에 대한 데이터가 존재할 경우.
                    // 던져줘야 하는 정보 -> 이 새끼의 시외 경로, 진짜 출발지 목적지의 위도 경도.
                    val intent = Intent(context, ShowDetailCityToCityActivity::class.java)
                    intent.putExtra("resultToDetailCityToCity", ArrayList(curPersonCityToCity))
                    intent.putExtra("sourceToDestination", curSourceToDes)
                    context.startActivity(intent)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return resultObjects.size
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

}