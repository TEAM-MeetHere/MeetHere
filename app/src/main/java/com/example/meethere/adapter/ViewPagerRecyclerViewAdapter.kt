package com.example.meethere.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.R
import com.example.meethere.activity.OtherRouteActivity
import com.example.meethere.activity.OtherRouteToShowDetail
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONException
import org.json.JSONObject

class ViewPagerRecyclerViewAdapter (var dataSet: List<ItemComponent>, var wholeDataSet : List<List<RouteItemComponent>>, context : Context):
    RecyclerView.Adapter<ViewPagerRecyclerViewAdapter.MyViewHolder>() {

    private var context = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerRecyclerViewAdapter.MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return ViewPagerRecyclerViewAdapter.MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val rvItemTotalTime : TextView = itemView.findViewById(R.id.rv_item_totalTime)
        val rvItemWalkAndFee : TextView = itemView.findViewById(R.id.rv_item_walk_and_fee)
        val rvItemStackedBarChart : TableLayout = itemView.findViewById(R.id.rv_item_horizontal_stackedbar)
        val rvItemApproxRoute : TextView = itemView.findViewById(R.id.rv_item_approx_route)
    }

    override fun onBindViewHolder(holder: ViewPagerRecyclerViewAdapter.MyViewHolder, position: Int) {
        val rvSingleItem = dataSet[position]

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
        if(insertRVitemApproxRoute.size == 6) holder.rvItemApproxRoute.setText("<"+insertRVitemApproxRoute[0]+ ">  " + insertRVitemApproxRoute[1] + "  " + "탑승  >  " + insertRVitemApproxRoute[4] + "하차\n")
        else{
            // list가 그 이상일 때. 하나의 교통 수단이 아닌 그 이상의 교통 수단으로 이동할 경우임.
            var prevTotalTime = insertRVitemApproxRoute[0]
            var prevTotalWalkAndFee = insertRVitemApproxRoute[1]
            holder.rvItemApproxRoute.setText("<"+prevTotalTime+">  " + prevTotalWalkAndFee + "  " + insertRVitemApproxRoute[2])
            // 여기서 줄바꿈을 하면 안됨.

            // 요기까지 하면 <몇 번>  <지 역>  <탑 승> 정도
            // 리스트가 6개 이상이기 때문에 분명 뭔가 환승 수단이 있을 거다. 그걸 판별해야함.
            var temp_i = 2
            for(i in 6 until insertRVitemApproxRoute.size step 6){
                // 첫 번째 교통 수단 이후의 수단을 기준으로 경로를 구분해줘야함 , last index에는 한 뭉치가 들어감.
                var calculate_last_index = temp_i * 6 - 1
                if(calculate_last_index + 1 == insertRVitemApproxRoute.size){
                    if(insertRVitemApproxRoute[i+1] == insertRVitemApproxRoute[i-2]){
                        holder.rvItemApproxRoute.append("\n\n"+"<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  환승  >  " + insertRVitemApproxRoute[i+4] + "  하차\n\n")
                    }// 이번에 출발지와 이전의 도착지가 같다면? 무조건 환승이지.
                    else{
                        holder.rvItemApproxRoute.append("  >  "+insertRVitemApproxRoute[i-2]+"에서 하차"+"\n\n")
                        holder.rvItemApproxRoute.append("<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  탑승  >  " + insertRVitemApproxRoute[i+4] + "  하차"+"\n")
                    }// 이번 출발지와 이전의 도착지가 다르다면?
                    break
                }
                else{
                    if(insertRVitemApproxRoute[i+1] == insertRVitemApproxRoute[i-2]){
                        holder.rvItemApproxRoute.append("\n\n"+"<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  환승")
                    }// 이번에 출발지와 이전의 도착지가 같다면? 무조건 환승이지.
                    else{
                        holder.rvItemApproxRoute.append("  >  "+insertRVitemApproxRoute[i-2]+"에서 하차"+"\n\n")
                        holder.rvItemApproxRoute.append("<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] +"  탑승")
                    }// 이번 출발지와 이전의 도착지가 다르다면?
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
        Log.d("하나의 테이블 구성 완료", "완료")
//
//
//            // 이걸 안넣어 도 되지. 이미 layout에 row가 형성되었으니까여 ㅇㅇ
        holder.rvItemStackedBarChart.addView(row)

        holder.itemView.setOnClickListener(){
            Log.d("클릭당했을 때", "result상에서의 해당 객체의 인덱스는 "+rvSingleItem.resultIndex.toString())

            Intent(context, OtherRouteToShowDetail::class.java).apply{
                putExtra("ClickToEachDetailTime", rvSingleItem.totalTime)
                putExtra("ClickToEachDetailWalk", rvSingleItem.walkTime)
                putExtra("ClickToEachDetailFee", rvSingleItem.totalFee)
                putExtra("ClickToEachDetailIndex", rvSingleItem.resultIndex)
                putExtra("ClickToEachDetailWholeRoute", ArrayList(dataSet))
                putExtra("ClickToEachDetailWholeDetailRoute", ArrayList(wholeDataSet))
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




//    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        // 요 안에 클릭 이벤트를 넣음.
//        // recycler view에 있는 각 layout에 item을 어떻게 넣을 건가
//        val rvItemTotalTime : TextView = itemView.findViewById(R.id.rv_item_totalTime)
//        val rvItemWalkAndFee : TextView = itemView.findViewById(R.id.rv_item_walk_and_fee)
//        val rvItemStackedBarChart : TableLayout = itemView.findViewById(R.id.rv_item_horizontal_stackedbar)
//        val rvItemApproxRoute : TextView = itemView.findViewById(R.id.rv_item_approx_route)
//
//        fun bind(position:Int){
//            Log.d("bind함수 ", "bind함수 시작")
//            val rvSingleItem = dataSet[position]
//
//            Log.d("rvSingleItem", dataSet[position].toString())
//            val insertRVitemApproxRoute = rvSingleItem.routesInfo
//            Log.d("APPROXROUTE", rvSingleItem.routesInfo.toString())
//            val insertRVstackedBarChart = rvSingleItem.totalTimeTable
//
//            if(rvSingleItem.totalTime >= 60){
//                val hour : Int = rvSingleItem.totalTime / 60
//                val miniute = rvSingleItem.totalTime % 60
//                if(miniute == 0){
//                    rvItemTotalTime.setText(""+hour+ "시간")
//                }else{
//                    rvItemTotalTime.setText(""+hour+"시간 "+miniute+"분")
//                }
//            }else{
//                rvItemTotalTime.setText(""+rvSingleItem.totalTime+"분")
//            }
//
//            // 오류 해결 1. 요금이 0원일 떄 요금 출력 안하는게 좋음
//            if(rvSingleItem.totalFee == 0){
//                rvItemWalkAndFee.setText("도보 " + rvSingleItem.walkTime + "m")
//            }
//            else {
//                rvItemWalkAndFee.setText("도보 " + rvSingleItem.walkTime + "m | " + rvSingleItem.totalFee)
//            }
//
//
//
//// 요기서부터 루트 출력하는 구간.
//            if(insertRVitemApproxRoute.size == 6) rvItemApproxRoute.setText("<"+insertRVitemApproxRoute[0]+ ">  " + insertRVitemApproxRoute[1] + "  " + "탑승  >  " + insertRVitemApproxRoute[4] + "하차\n")
//            else{
//                // list가 그 이상일 때. 하나의 교통 수단이 아닌 그 이상의 교통 수단으로 이동할 경우임.
//                var prevTotalTime = insertRVitemApproxRoute[0]
//                var prevTotalWalkAndFee = insertRVitemApproxRoute[1]
//                rvItemApproxRoute.setText("<"+prevTotalTime+">  " + prevTotalWalkAndFee + "  " + insertRVitemApproxRoute[2])
//                // 여기서 줄바꿈을 하면 안됨.
//
//                // 요기까지 하면 <몇 번>  <지 역>  <탑 승> 정도
//                // 리스트가 6개 이상이기 때문에 분명 뭔가 환승 수단이 있을 거다. 그걸 판별해야함.
//                var temp_i = 2
//                for(i in 6 until insertRVitemApproxRoute.size step 6){
//                    // 첫 번째 교통 수단 이후의 수단을 기준으로 경로를 구분해줘야함 , last index에는 한 뭉치가 들어감.
//                    var calculate_last_index = temp_i * 6 - 1
//                    if(calculate_last_index + 1 == insertRVitemApproxRoute.size){
//                        if(insertRVitemApproxRoute[i+1] == insertRVitemApproxRoute[i-2]){
//                            rvItemApproxRoute.append("\n\n"+"<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  환승  >  " + insertRVitemApproxRoute[i+4] + "  하차\n\n")
//                        }// 이번에 출발지와 이전의 도착지가 같다면? 무조건 환승이지.
//                        else{
//                            rvItemApproxRoute.append("  >  "+insertRVitemApproxRoute[i-2]+"에서 하차"+"\n\n")
//                            rvItemApproxRoute.append("<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  탑승  >  " + insertRVitemApproxRoute[i+4] + "  하차"+"\n")
//                        }// 이번 출발지와 이전의 도착지가 다르다면?
//                        break
//                    }
//                    else{
//                        if(insertRVitemApproxRoute[i+1] == insertRVitemApproxRoute[i-2]){
//                            rvItemApproxRoute.append("\n\n"+"<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] + "  환승")
//                        }// 이번에 출발지와 이전의 도착지가 같다면? 무조건 환승이지.
//                        else{
//                            rvItemApproxRoute.append("  >  "+insertRVitemApproxRoute[i-2]+"에서 하차"+"\n\n")
//                            rvItemApproxRoute.append("<"+insertRVitemApproxRoute[i] + ">  " + insertRVitemApproxRoute[i+1] +"  탑승")
//                        }// 이번 출발지와 이전의 도착지가 다르다면?
//                        temp_i++
//                    }
//                }
//            }
//
//// 요기까지
//            // table calculate (총 시간)
//            var wholeTableWeight : Int = 0
//            for(i in 0 until insertRVstackedBarChart.size){
//                wholeTableWeight = wholeTableWeight + insertRVstackedBarChart[i].second
//            }
//
//
//            var row = TableRow(context)
//            var param1 = TableRow.LayoutParams(
//                TableLayout.LayoutParams.MATCH_PARENT,
//                0,
//                wholeTableWeight.toFloat()
//            )
//            row.layoutParams = param1
//
//            Log.d("하나의 테이블 구성 시작", "ㅇㅋ")
//            for(i in 0 until insertRVstackedBarChart.size){
//                var text = TextView(context)
//                var text_layout : TableRow.LayoutParams
//                if(insertRVstackedBarChart[i].second < 10) {
//                    text_layout = TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 10f)
//                }
//
//                else {
//                    text_layout = TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, insertRVstackedBarChart[i].second.toFloat())
//                }
//                text.layoutParams = text_layout
//                text.textAlignment = View.TEXT_ALIGNMENT_CENTER
//                Log.d("하나의 loop을 돌면서 시간", insertRVstackedBarChart[i].toString())
//                Log.d("패딩까지", "완료")
//                if(insertRVstackedBarChart[i].first == 1){
//                    text.setText(""+ insertRVstackedBarChart[i].second+"분")
//                    text.setBackgroundColor(Color.parseColor("#C0FFFF"))
//                }else if(insertRVstackedBarChart[i].first == 2) {
//                    text.setText(""+ insertRVstackedBarChart[i].second +"분")
//                    text.setBackgroundColor(Color.parseColor("#D2FFD2"))
//                }else{
//                    text.setText(""+ insertRVstackedBarChart[i].second +"분")
//                    text.setBackgroundColor(Color.parseColor("#FFB4FF"))
//                }
//                row.addView(text)
//            }
//            Log.d("하나의 테이블 구성 완료", "완료")
////
////
////            // 이걸 안넣어 도 되지. 이미 layout에 row가 형성되었으니까여 ㅇㅇ
//            rvItemStackedBarChart.addView(row)
//
////            itemView.setOnClickListener(){
////                Log.d("클릭당했을 때", "result상에서의 해당 객체의 인덱스는 "+rvSingleItem.resultIndex.toString())
////                Log.d("클릭당했을 때", "출발지 위도 경도 "+sourceX+", "+sourceY+"목적지 위도 경도"+desX +", "+desY)
////            }
//
//            Log.d("bind함수 끝", "bind끝")
//        }
//
//
//        // 해당 리스트 아이템 하나를 구성할 레이아웃은 다 만들어주고 그럼 해야할일은? 이걸 눌렀을 때 어떻게 되었으면 좋겠는지
//        // 일단 간단한게 토스트형식으로 정보들을 띄워주자.
//    }
}

//fun TextView.addBorder(
//    color : Int = Color.RED,
//    width : Float = 10F
//){
//    val drawable = ShapeDrawable().apply{
//        shape = RectShape()
//        paint.apply{
//            this.color = color
//            strokeWidth=width
//            style = Paint.Style.STROKE
//        }
//    }
//
//    background = drawable
//}