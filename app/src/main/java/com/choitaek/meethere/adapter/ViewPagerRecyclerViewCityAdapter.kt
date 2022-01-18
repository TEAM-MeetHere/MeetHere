package com.choitaek.meethere.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.choitaek.meethere.R
import com.choitaek.meethere.activity.OtherRouteToShowDetailCityToCity
import com.choitaek.meethere.objects.ItemComponent
import com.choitaek.meethere.objects.RouteItemComponent

class ViewPagerRecyclerViewCityAdapter(var dataSet: List<ItemComponent>, var indexSet : List<Int>, var wholeDataSet : List<List<RouteItemComponent>>, context : Context):
    RecyclerView.Adapter<ViewPagerRecyclerViewCityAdapter.CityViewHolder>() {

    private var context = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerRecyclerViewCityAdapter.CityViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return ViewPagerRecyclerViewCityAdapter.CityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class CityViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val rvItemTotalTime : TextView = itemView.findViewById(R.id.rv_item_totalTime)
        val rvItemWalkAndFee : TextView = itemView.findViewById(R.id.rv_item_walk_and_fee)
        val rvItemStackedBarChart : TableLayout = itemView.findViewById(R.id.rv_item_horizontal_stackedbar)
        val rvItemApproxRoute : TextView = itemView.findViewById(R.id.rv_item_approx_route)
    }

    override fun onBindViewHolder(holder: ViewPagerRecyclerViewCityAdapter.CityViewHolder, position: Int) {
        val rvSingleItem = dataSet[position]
        val rvIndex = indexSet[position]

        Log.d("rvSingleItem", dataSet[position].toString())
        val insertRVitemApproxRoute = rvSingleItem.routesInfo
        Log.d("APPROXROUTE", rvSingleItem.routesInfo.toString())
        val insertRVstackedBarChart = rvSingleItem.totalTimeTable

        if(rvSingleItem.totalTime >= 60){
            val hour : Int = rvSingleItem.totalTime / 60
            val miniute = rvSingleItem.totalTime % 60
            if(miniute == 0){
                holder.rvItemTotalTime.setText(""+hour+ "시간")
            }else{
                holder.rvItemTotalTime.setText(""+hour+"시간 "+miniute+"분")
            }
        }else{
            holder.rvItemTotalTime.setText(""+rvSingleItem.totalTime+"분")
        }

        // 오류 해결 1. 요금이 0원일 떄 요금 출력 안하는게 좋음
        holder.rvItemWalkAndFee.setText("도보 " + rvSingleItem.walkTime + "분")


        for(i in 0 until insertRVitemApproxRoute.size step 6){
            Log.d("for문안의 문자열", insertRVitemApproxRoute[i])
            if(insertRVitemApproxRoute[i].length > 10){
                Log.d("해당 길이를 초과하는 문자열 ", insertRVitemApproxRoute[i])
                val startIndex:Int = insertRVitemApproxRoute[i].indexOf('(')
                if(startIndex != -1){
                    //찾았다는 얘기이므로 split하기
                    Log.d("(을 찾았습니다","ㅇ")
                    insertRVitemApproxRoute[i] = insertRVitemApproxRoute[i].substring(0 until startIndex)
                }
            }
        }


// 요기서부터 루트 출력하는 구간.
        if(insertRVitemApproxRoute.size == 6) {
            if(insertRVitemApproxRoute[0] == "버스"){
                holder.rvItemApproxRoute.setText("< 시외버스 >  " + insertRVitemApproxRoute[1] + "  " + "탑승  -->  " + insertRVitemApproxRoute[4] + "하차\n")
            }
            else if(insertRVitemApproxRoute[0] == "열차"){
                holder.rvItemApproxRoute.setText("< 열차 >  " + insertRVitemApproxRoute[1] + "  " + "탑승  -->  " + insertRVitemApproxRoute[4] + "하차\n")
            }
            else if(insertRVitemApproxRoute[0] == "비행기"){
                holder.rvItemApproxRoute.setText("< 비행기 >  " + insertRVitemApproxRoute[1] + "  " + "탑승  -->  " + insertRVitemApproxRoute[4] + "하차\n")
            }
            else {
                holder.rvItemApproxRoute.setText("<" + insertRVitemApproxRoute[0] + ">  " + insertRVitemApproxRoute[1] + "  " + "탑승  >  " + insertRVitemApproxRoute[4] + "하차\n")
            }
        }
        else{
            // list가 그 이상일 때. 하나의 교통 수단이 아닌 그 이상의 교통 수단으로 이동할 경우임.
            var loop_flag = false
            var prevTotalTime = insertRVitemApproxRoute[0]
            var prevTotalWalkAndFee = insertRVitemApproxRoute[1]
            if(insertRVitemApproxRoute[0] == "버스"){
                Log.d("첫 묶음", "시외버스")
                holder.rvItemApproxRoute.setText("< 시외버스 >  " + insertRVitemApproxRoute[1] + "  " + "탑승  -->  " + insertRVitemApproxRoute[4] + "하차\n")
                loop_flag = true
            }
            else if(insertRVitemApproxRoute[0] == "열차"){
                Log.d("첫 묶음", "열차")
                holder.rvItemApproxRoute.setText("< 열차 >  " + insertRVitemApproxRoute[1] + "  " + "탑승  -->  " + insertRVitemApproxRoute[4] + "하차\n")
                loop_flag = true
            }
            else if(insertRVitemApproxRoute[0] == "비행기"){
                Log.d("첫 묶음", "ㅂ행기")
                holder.rvItemApproxRoute.setText("< 비행기 >  " + insertRVitemApproxRoute[1] + "  " + "탑승  -->  " + insertRVitemApproxRoute[4] + "하차\n")
                loop_flag = true
            }
            else {
                Log.d("첫 묶음", "이도저도아님")
                holder.rvItemApproxRoute.setText("<"+prevTotalTime+">  " + prevTotalWalkAndFee + "  " + insertRVitemApproxRoute[2])
                loop_flag = false
            }
            // 여기서 줄바꿈을 하면 안됨.

            // 요기까지 하면 <몇 번>  <지 역>  <탑 승> 정도
            // 리스트가 6개 이상이기 때문에 분명 뭔가 환승 수단이 있을 거다. 그걸 판별해야함.
            var temp_i = 2
            for(i in 6 until insertRVitemApproxRoute.size step 6){
                var calculate_last_index = temp_i * 6 - 1
                if(calculate_last_index + 1 == insertRVitemApproxRoute.size){
                    // 마지막 묶음.
                    if(insertRVitemApproxRoute[i] == "버스" || insertRVitemApproxRoute[i] == "열차" || insertRVitemApproxRoute[i] == "비행기"){
                        if(loop_flag == true){
                            if(insertRVitemApproxRoute[i] == "버스") holder.rvItemApproxRoute.append("< 시외 버스 > "+insertRVitemApproxRoute[i+1]+"탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n")
                            else if(insertRVitemApproxRoute[i] == "열차") holder.rvItemApproxRoute.append("< 시외 버스 > "+insertRVitemApproxRoute[i+1]+"탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n")
                            if(insertRVitemApproxRoute[i] == "비행기") holder.rvItemApproxRoute.append("< 시외 버스 > "+insertRVitemApproxRoute[i+1]+"탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n")
                        }else{
                            holder.rvItemApproxRoute.append(" --> " + insertRVitemApproxRoute[i-2] + "  하차\n\n")
                            if(insertRVitemApproxRoute[i] == "버스") holder.rvItemApproxRoute.append("< 시외 버스 > "+insertRVitemApproxRoute[i+1]+"  탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            else if(insertRVitemApproxRoute[i] == "열차") holder.rvItemApproxRoute.append("< 열차 > "+insertRVitemApproxRoute[i+1]+"  탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            if(insertRVitemApproxRoute[i] == "비행기") holder.rvItemApproxRoute.append("< 비행기 > "+insertRVitemApproxRoute[i+1]+"  탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                        }
                        break
                    }
                    else{
                        if(insertRVitemApproxRoute[i + 1] == insertRVitemApproxRoute[i - 2]){
                            if(loop_flag == true){
                                holder.rvItemApproxRoute.append("< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  탑승 --> "+ insertRVitemApproxRoute[i+4] + " 하차\n")
                            }
                            else{
                                holder.rvItemApproxRoute.append("\n\n< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  환승 --> "+ insertRVitemApproxRoute[i+4] + " 하차\n")
                            }
                        }
                        else{
                            if(loop_flag == true){
                                holder.rvItemApproxRoute.append("< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  탑승 --> "+ insertRVitemApproxRoute[i+4] + " 하차\n")
                            }
                            else{
                                holder.rvItemApproxRoute.append("  -->  "+insertRVitemApproxRoute[i-2]+" 하차\n\n" + " < "+insertRVitemApproxRoute[i]+" >   "+insertRVitemApproxRoute[i+1] +"  탑승 --> "+ insertRVitemApproxRoute[i+4] + " 하차\n")
                            }
                        }
                        break
                    }
                }
                else{
                    if(insertRVitemApproxRoute[i] == "버스" || insertRVitemApproxRoute[i] == "열차" || insertRVitemApproxRoute[i] == "비행기"){
                        // 마지막 묶음이 아니면서 현재가 시외수단일 경우
                        if(loop_flag == true){
                            if(insertRVitemApproxRoute[i] == "버스") holder.rvItemApproxRoute.append("< 시외 버스 > "+insertRVitemApproxRoute[i+1]+"탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            else if(insertRVitemApproxRoute[i] == "열차") holder.rvItemApproxRoute.append("< 열차 > "+insertRVitemApproxRoute[i+1]+"탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            if(insertRVitemApproxRoute[i] == "비행기") holder.rvItemApproxRoute.append("< 비행기 > "+insertRVitemApproxRoute[i+1]+"탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                        }
                        else{
                            // 마지막 묶음이 아니면서 현재가 시외수단이면서 전 경로가 불완전하게 끝났을 때.
                            holder.rvItemApproxRoute.append(" --> " + insertRVitemApproxRoute[i-2] + "  하차\n\n")
                            if(insertRVitemApproxRoute[i] == "버스") holder.rvItemApproxRoute.append("< 시외 버스 > "+insertRVitemApproxRoute[i+1]+"  탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            else if(insertRVitemApproxRoute[i] == "열차") holder.rvItemApproxRoute.append("< 열차 > "+insertRVitemApproxRoute[i+1]+"  탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            if(insertRVitemApproxRoute[i] == "비행기") holder.rvItemApproxRoute.append("< 비행기 > "+insertRVitemApproxRoute[i+1]+"  탑승  -->  " + insertRVitemApproxRoute[i + 4] + "  하차\n\n")
                            // loop_flag가 false일 경우.
                        }
                        loop_flag = true
                    }

                    else{
                        // 마지막 묶음이 아니면서 현재가 시외수단이 아닐 경우
                        if(insertRVitemApproxRoute[i + 1] == insertRVitemApproxRoute[i - 2]){
                            if(loop_flag == true){
                                // 이전 도착 == 이번 출발 + 이전 경로 완전
                                holder.rvItemApproxRoute.append("< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  탑승")
                                loop_flag = false
                            }
                            else{
                                // flag가 false
                                // 이전 도착 == 이번 출발 + 이전 경로 불완전
                                holder.rvItemApproxRoute.append("\n\n" + "< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  환승")
                                loop_flag = false
                            }
                        }
                        else{
                            if(loop_flag == true){
                                holder.rvItemApproxRoute.append("< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  탑승")
                                loop_flag = false
                            }
                            else{
                                holder.rvItemApproxRoute.append(" -->  "+insertRVitemApproxRoute[i-2]+" 하차\n\n"+"< "+insertRVitemApproxRoute[i]+" >  " + insertRVitemApproxRoute[i+1] +"  탑승")
                                loop_flag = false
                            }
                        }
                    }
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
            0,
            wholeTableWeight.toFloat()
        )
        row.layoutParams = param1

        Log.d("하나의 테이블 구성 시작", "ㅇㅋ")
        for(i in 0 until insertRVstackedBarChart.size){
            var text = TextView(context)
            var text_layout : TableRow.LayoutParams
            if(insertRVstackedBarChart.size >= 8){
                if (insertRVstackedBarChart[i].second < 10 && insertRVstackedBarChart[i].second > 0) {
                    Log.d("0에서 10의 시간", "ㅇ")
                    text_layout =
                        TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 10f)
                } else if (insertRVstackedBarChart[i].second >= 30) {
                    Log.d("0에서 10의 시간", "ㅇ")
                    text_layout =
                        TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 30f)
                } else if (insertRVstackedBarChart[i].second >= 10) {
                    Log.d("10에서 100이하의 시간", "ㅇ")
                    text_layout = TableRow.LayoutParams(0,
                        TableLayout.LayoutParams.WRAP_CONTENT,
                        insertRVstackedBarChart[i].second.toFloat())
                } else {
                    Log.d("시간이 0", "0")
                    continue
                }
            }
            else {
                if (insertRVstackedBarChart[i].second < 10 && insertRVstackedBarChart[i].second > 0) {
                    Log.d("0에서 10의 시간", "ㅇ")
                    text_layout =
                        TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 10f)
                } else if (insertRVstackedBarChart[i].second >= 50) {
                    Log.d("0에서 10의 시간", "ㅇ")
                    text_layout =
                        TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 50f)
                } else if (insertRVstackedBarChart[i].second >= 10) {
                    Log.d("10에서 100이하의 시간", "ㅇ")
                    text_layout = TableRow.LayoutParams(0,
                        TableLayout.LayoutParams.WRAP_CONTENT,
                        insertRVstackedBarChart[i].second.toFloat())
                } else {
                    Log.d("시간이 0", "0")
                    continue
                }
            }
            text.layoutParams = text_layout
            text.textAlignment = View.TEXT_ALIGNMENT_CENTER
            Log.d("하나의 loop을 돌면서 시간", insertRVstackedBarChart[i].toString())
            Log.d("패딩까지", "완료")
            if(insertRVstackedBarChart[i].first == 1){
                Log.d("1이 호출","ㅇ")
                text.setText(""+ insertRVstackedBarChart[i].second+"분")
                text.setBackgroundColor(Color.parseColor("#C0FFFF"))
            }else if(insertRVstackedBarChart[i].first == 2) {
                Log.d("2이 호출","ㅇ")
                text.setText(""+ insertRVstackedBarChart[i].second +"분")
                text.setBackgroundColor(Color.parseColor("#D2FFD2"))
            }else if(insertRVstackedBarChart[i].first == 3){
                Log.d("3이 호출","ㅇ")
                text.setText(""+ insertRVstackedBarChart[i].second +"분")
                text.setBackgroundColor(Color.parseColor("#FFB4FF"))
            }else if(insertRVstackedBarChart[i].first == 4){
                Log.d("4이 호출","ㅇ")
                text.setText(""+ insertRVstackedBarChart[i].second +"분")
                text.setBackgroundColor(Color.parseColor("#FAFAA0"))
            }else if(insertRVstackedBarChart[i].first == 5){
                Log.d("5이 호출","ㅇ")
                text.setText(""+ insertRVstackedBarChart[i].second +"분")
                text.setBackgroundColor(Color.parseColor("#F5D08A"))
            }else{
                Log.d("6이 호출","ㅇ")
                text.setText(""+ insertRVstackedBarChart[i].second +"분")
                text.setBackgroundColor(Color.parseColor("#dcdcdc"))
            }
            row.addView(text)
        }
        Log.d("하나의 테이블 구성 완료", "완료")
//
//
//            // 이걸 안넣어 도 되지. 이미 layout에 row가 형성되었으니까여 ㅇㅇ
        holder.rvItemStackedBarChart.addView(row)

        holder.itemView.setOnClickListener(){
            Log.d("클릭당했을 때", "result상에서의 해당 객체의 인덱스는 "+rvSingleItem.resultIndex.toString())

            Intent(context, OtherRouteToShowDetailCityToCity::class.java).apply{
                putExtra("ClickToEachDetailTimeCityToCity", rvSingleItem.totalTime)
                putExtra("ClickToEachDetailWalkCityToCity", rvSingleItem.walkTime)
                putExtra("ClickToEachDetailFeeCityToCity", rvSingleItem.totalFee)
                putExtra("ClickToEachDetailIndexCityToCity", rvIndex)
                putExtra("ClickToEachDetailWholeRouteCityToCity", ArrayList(dataSet))
                putExtra("ClickToEachDetailWholeDetailRouteCityToCity", ArrayList(wholeDataSet))
            }.run {context.startActivity(this)}
        }

        Log.d("bind함수 끝", "bind끝")
//        holder.bind(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
