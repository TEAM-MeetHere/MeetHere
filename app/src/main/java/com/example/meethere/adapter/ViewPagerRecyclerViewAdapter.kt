package com.example.meethere.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.R
import com.example.meethere.objects.ItemComponent

class ViewPagerRecyclerViewAdapter (var dataSet: List<ItemComponent>, context : Context): RecyclerView.Adapter<ViewPagerRecyclerViewAdapter.MyViewHolder>() {
    private var context = context
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // recycler view에 있는 각 layout에 item을 어떻게 넣을 건가
        val rvItemTotalTime : TextView = itemView.findViewById(R.id.rv_item_totalTime)
        val rvItemWalkAndFee : TextView = itemView.findViewById(R.id.rv_item_walk_and_fee)
        val rvItemStackedBarChart : TableLayout = itemView.findViewById(R.id.rv_item_horizontal_stackedbar)
        val rvItemApproxRoute : TextView = itemView.findViewById(R.id.rv_item_approx_route)

        fun bind(position:Int){
            Log.d("bind함수 ", "여기까지 오냐?")
            val rvSingleItem = dataSet[position]

            Log.d("rvSingleItem", dataSet[position].toString())
            val insertRVitemApproxRoute = rvSingleItem.routesInfo
            Log.d("APPROXROUTE", rvSingleItem.routesInfo.toString())
            val insertRVstackedBarChart = rvSingleItem.totalTimeTable

            if(rvSingleItem.totalTime >= 60){
                val hour : Int = rvSingleItem.totalTime / 60
                val miniute = rvSingleItem.totalTime % 60
                if(miniute == 0){
                    rvItemTotalTime.setText(""+hour+ "시간")
                }else{
                    rvItemTotalTime.setText(""+hour+"시간 "+miniute+"분")
                }
            }else{
                rvItemTotalTime.setText(""+rvSingleItem.totalTime+"분")
            }
            rvItemWalkAndFee.setText("도보 " + rvSingleItem.walkTime + "m | " + rvSingleItem.totalFee)



// 요기서부터 루트 출력하는 구간.
            if(insertRVitemApproxRoute.size == 6) rvItemApproxRoute.setText("<"+insertRVitemApproxRoute[0]+ ">  " + insertRVitemApproxRoute[1] + "  " + "탑승  >  " + insertRVitemApproxRoute[4] + "하차\n")
            else{
                // list가 그 이상일 때. 하나의 교통 수단이 아닌 그 이상의 교통 수단으로 이동할 경우임.
                var prevTotalTime = insertRVitemApproxRoute[0]
                var prevTotalWalkAndFee = insertRVitemApproxRoute[1]
                rvItemApproxRoute.setText("<"+prevTotalTime+">  " + prevTotalWalkAndFee + "  " + insertRVitemApproxRoute[2]+"\n\n")

                // 요기까지 하면 <몇 번>  <지 역>  <탑 승> 정도
                // 리스트가 6개 이상이기 때문에 분명 뭔가 환승 수단이 있을 거다. 그걸 판별해야함.
                var temp_i = 2
                for(i in 6 until insertRVitemApproxRoute.size step 6){
                    // 첫 번째 교통 수단 이후의 수단을 기준으로 경로를 구분해줘야함 , last index에는 한 뭉치가 들어감.
                    var calculate_last_index = temp_i * 6 - 1
                    if(calculate_last_index + 1 == insertRVitemApproxRoute.size){
                        // 이 경우 이 때의 한 뭉치로 경로 끝임 즉 환승 하차가 들어감.
                        rvItemApproxRoute.append("<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  환승  >  " + insertRVitemApproxRoute[i+4] + "  하차\n")
                        break
                    }
                    else{
                        rvItemApproxRoute.append("<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  환승\n\n")
                        temp_i++
                    }
                }
            }

// 요기까지
            // table calculate (총 시간)
            var wholeTableWeight : Int = 0
            for(i in 0 until insertRVstackedBarChart.size){
                wholeTableWeight = wholeTableWeight + insertRVstackedBarChart[i].second
            }


            var row = TableRow(context)
            var param1 = TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                wholeTableWeight.toFloat()
            )
            row.layoutParams = param1

            for(i in 0 until insertRVstackedBarChart.size){
                var text = TextView(context)
                var text_layout : TableRow.LayoutParams
                if(insertRVstackedBarChart[i].second < 10) {
                    text_layout = TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 10f)
                }

                else {
                    text_layout = TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, insertRVstackedBarChart[i].second.toFloat())
                }
                text.layoutParams = text_layout
                text.textAlignment = View.TEXT_ALIGNMENT_CENTER
                Log.d("패딩까지", "완료")
                if(insertRVstackedBarChart[i].first == 1){
                    text.setText(""+ insertRVstackedBarChart[i].second+"분")
                    text.setBackgroundColor(Color.parseColor("#C0FFFF"))
                }else if(insertRVstackedBarChart[i].first == 2) {
                    text.setText(""+ insertRVstackedBarChart[i].second +"분")
                    text.setBackgroundColor(Color.parseColor("#D2FFD2"))
                }else{
                    text.setText(""+ insertRVstackedBarChart[i].second +"분")
                    text.setBackgroundColor(Color.parseColor("#FFB4FF"))
                }
                row.addView(text)
            }
//
//
//            // 이걸 안넣어 도 되지. 이미 layout에 row가 형성되었으니까여 ㅇㅇ
            rvItemStackedBarChart.addView(row)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context =parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_route, parent, false)
        Log.d("create view holder", "Dd")
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}