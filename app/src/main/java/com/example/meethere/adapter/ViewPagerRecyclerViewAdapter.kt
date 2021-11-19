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

class ViewPagerRecyclerViewAdapter (var dataSet: List<ItemComponent>, sx : String, sy : String, dx: String, dy : String, context : Context):
    RecyclerView.Adapter<ViewPagerRecyclerViewAdapter.MyViewHolder>() {
    private var odsayService: ODsayService? = null
    private var jsonObject: JSONObject? = null

    private var context = context
    private val sourceX = sx
    private val sourceY = sy
    private val desX = dx
    private val desY = dy

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerRecyclerViewAdapter.MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return ViewPagerRecyclerViewAdapter.MyViewHolder(view)
//        val context =parent.context
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.item_route, parent, false)
//        Log.d("create view holder", "Dd")
//        return MyViewHolder(view)
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
        if(rvSingleItem.totalFee == 0){
            holder.rvItemWalkAndFee.setText("도보 " + rvSingleItem.walkTime + "m")
        }
        else {
            holder.rvItemWalkAndFee.setText("도보 " + rvSingleItem.walkTime + "m | " + rvSingleItem.totalFee)
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
            if(insertRVstackedBarChart[i].second < 10 && insertRVstackedBarChart[i].second > 0) {
                text_layout = TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 10f)
            }

            else {
                text_layout = TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, insertRVstackedBarChart[i].second.toFloat())
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
            Log.d("클릭당했을 때", "출발지 위도 경도 "+sourceX+", "+sourceY+"목적지 위도 경도"+desX +", "+desY)

            odsayService = ODsayService.init(context, "hQVkqz/l8aEPCdgn6JlDWk793L3D/rl5Cyko3JqKhcw") // api가져옴.

            var onResultCallbackListener: OnResultCallbackListener =
                object : OnResultCallbackListener {
                    override fun onSuccess(ODsayData: ODsayData?, api: API?) {
                        Log.d("API호출 성공", "성공")
                        jsonObject = ODsayData!!.json
                        try {
                            if(api == API.SEARCH_PUB_TRANS_PATH){
                                var one_person_detail_route_array: MutableList<RouteItemComponent> =
                                    arrayListOf()
                                var result = ODsayData.json.getJSONObject("result")
                                var resultBest = result.getJSONArray("path")
                                var targetOBJ = resultBest.getJSONObject(rvSingleItem.resultIndex)
                                var targetOBJInfo = targetOBJ.getJSONObject("info")

                                var min_time = targetOBJInfo.getInt("totalTime")
                                var min_distance = targetOBJInfo.getInt("totalWalk")
                                var min_payment = targetOBJInfo.getInt("payment")

                                var minResultPathObjectSubPath =
                                    targetOBJ.getJSONArray("subPath")

                                var layoutType = -1
                                var trafficType = -1
                                var distance = -1
                                var sectionTime = -1
                                var stationCount = -1
                                var busNoORname = "NoData"
                                var startName = "NoData"
                                var endName = "NoData"
                                var passStopList: MutableList<Pair<String, String>> = arrayListOf()
                                var door = "NoData"
                                var startExitnoORendExitno = "NoData"
                                var way = "NoData"

                                for (i in 0 until minResultPathObjectSubPath.length()) {
                                    if (i == 0) {
                                        //첫 번째 인덱스에 대한 case
                                        var currentRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(0)
                                        var tempNextRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(1)
                                        if (tempNextRouteObject.getInt("trafficType") == 2) {
                                            // 도보 -> 버스 탈 경우
                                            layoutType = 6
                                            sectionTime = currentRouteObject.getInt("sectionTime")
                                            distance = currentRouteObject.getInt("distance")
                                        } else {
                                            // 도보 -> 지하철 탈 경우
                                            layoutType = 6
                                            sectionTime = currentRouteObject.getInt("sectionTime")
                                            distance = currentRouteObject.getInt("distance")
                                            startName = tempNextRouteObject.getString("startName")
                                            startExitnoORendExitno =
                                                tempNextRouteObject.getString("startExitNo")
                                        }
                                    } else if (i < minResultPathObjectSubPath.length() - 1) {
                                        // 나머지 인덱스에 대한 case(마지막 인덱스에 대한 case는 도보이므로 제외)

                                        var prevRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(i - 1)
                                        var currentRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(i)
                                        var nextRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(i + 1)
                                        if (currentRouteObject.getInt("trafficType") == 3) {
                                            // 현재가 도보일 경우.
                                            if (prevRouteObject.getInt("trafficType") == 2) {
                                                if (currentRouteObject.getInt("sectionTime") == 0) {
                                                    //이전에 버스타고 이번에 도보인데 0분이라면 해당 정류장에서 버스를 갈아타는 것
                                                    layoutType = 8
                                                    endName = prevRouteObject.getString("endName")
                                                } else {
                                                    // 이전에 버스타고 이번에 도보인데 0분이 아닐 경우 일단은 걷겠지.
                                                    if (nextRouteObject.getInt("trafficType") == 1) {
                                                        // 다음 수단이 지하철이라면? 어디역 몇 번 출구까지 몇 m에 대한 정보가 필요
                                                        layoutType = 7
                                                        sectionTime =
                                                            currentRouteObject.getInt("sectionTime")
                                                        endName = prevRouteObject.getString("endName")
                                                        startName =
                                                            nextRouteObject.getString("startName")
                                                        startExitnoORendExitno =
                                                            nextRouteObject.getString("startExitNo")
                                                        distance = currentRouteObject.getInt("distance")
                                                    } else {
                                                        // 다음 수단이 버스다. 그럼 그냥 기본 7번 레이아웃을 사용
                                                        layoutType = 7
                                                        sectionTime =
                                                            currentRouteObject.getInt("sectionTime")
                                                        endName = prevRouteObject.getString("endName")
                                                        distance = currentRouteObject.getInt("distance")

                                                    }
                                                }
                                            } else {
                                                // 이전 경로가 지하철일 경우.
                                                if (prevRouteObject.getString("door") == "null") {
                                                    // 이전에 지하철 타고 왔는데 환승이 아니라 그냥 내릴 경우.
                                                    layoutType = 5
                                                    sectionTime =
                                                        currentRouteObject.getInt("sectionTime")
                                                    endName = prevRouteObject.getString("endName")
                                                    startExitnoORendExitno =
                                                        prevRouteObject.getString("endExitNo")
                                                    distance = currentRouteObject.getInt("distance")
                                                } else {
                                                    // 이전에 지하철 탔는데 이번에 환승할 경우
                                                    layoutType = 4
                                                    endName = prevRouteObject.getString("endName")
                                                }
                                            }
                                        } else if (currentRouteObject.getInt("trafficType") == 2) {
                                            // 현재가 버스일 경우
                                            // 무조건 layout이 3번임 예외 없음
                                            layoutType = 3
                                            sectionTime = currentRouteObject.getInt("sectionTime")
                                            startName = currentRouteObject.getString("startName")

                                            val currentRouteObjectLane =
                                                currentRouteObject.getJSONArray("lane")
                                            val currentRouteObjectLaneFirstInfo =
                                                currentRouteObjectLane.getJSONObject(0)
                                            busNoORname =
                                                currentRouteObjectLaneFirstInfo.getString("busNo")
                                            Log.d("각 케이스에서의 역 개수가 알고 싶어용",currentRouteObject.getInt("stationCount").toString() )

                                            val currentRouteObjectPassStopList =
                                                currentRouteObject.getJSONObject("passStopList")
                                            val currentRouteObjectPassStopListStations =
                                                currentRouteObjectPassStopList.getJSONArray("stations")
                                            for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                                // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자.
                                                val currentRouteObjectPassStopListStationsObj =
                                                    currentRouteObjectPassStopListStations.getJSONObject(j)
                                                passStopList.add(Pair(
                                                    currentRouteObjectPassStopListStationsObj.getString(
                                                        "stationName"),
                                                    currentRouteObjectPassStopListStationsObj.getString(
                                                        "isNonStop")))
                                            }
                                            stationCount = currentRouteObjectPassStopListStations.length() - 1
                                        } else {
                                            // 현재 경로가 지하철일 경우, view type은 1,2번이지
                                            if (nextRouteObject.getInt("sectionTime") != 0) {
                                                // 환승에 대한 정보가 없을 경우 -> null 이 때는 layout type이 2번
                                                layoutType = 2
                                                sectionTime = currentRouteObject.getInt("sectionTime")

                                                val currentRouteObjectLane =
                                                    currentRouteObject.getJSONArray("lane")
                                                val currentRouteObjectLaneFirstInfo =
                                                    currentRouteObjectLane.getJSONObject(0)
                                                busNoORname =
                                                    currentRouteObjectLaneFirstInfo.getString("name")

                                                startName = currentRouteObject.getString("startName")

                                                way = currentRouteObject.getString("way")

                                                val currentRouteObjectPassStopList =
                                                    currentRouteObject.getJSONObject("passStopList")
                                                val currentRouteObjectPassStopListStations =
                                                    currentRouteObjectPassStopList.getJSONArray("stations")
                                                for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                                    // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자. 하지만 유의할 건 지하철이므로 미정차 여부는 N으로 채움,
                                                    val currentRouteObjectPassStopListStationsObj =
                                                        currentRouteObjectPassStopListStations.getJSONObject(j)
                                                    passStopList.add(Pair(
                                                        currentRouteObjectPassStopListStationsObj.getString(
                                                            "stationName"),
                                                        "N"))
                                                }
                                                stationCount = currentRouteObjectPassStopListStations.length() - 1
                                            } else {
                                                // 환승에 대한 정보가 존재할 경우
                                                layoutType = 1
                                                sectionTime = currentRouteObject.getInt("sectionTime")

                                                val currentRouteObjectLane =
                                                    currentRouteObject.getJSONArray("lane")
                                                val currentRouteObjectLaneFirstInfo =
                                                    currentRouteObjectLane.getJSONObject(0)
                                                busNoORname =
                                                    currentRouteObjectLaneFirstInfo.getString("name")

                                                startName = currentRouteObject.getString("startName")

                                                way = currentRouteObject.getString("way")
                                                door = currentRouteObject.getString("door")

                                                val currentRouteObjectPassStopList =
                                                    currentRouteObject.getJSONObject("passStopList")
                                                val currentRouteObjectPassStopListStations =
                                                    currentRouteObjectPassStopList.getJSONArray("stations")
                                                for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                                    // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자. 하지만 유의할 건 지하철이므로 미정차 여부는 N으로 채움,
                                                    val currentRouteObjectPassStopListStationsObj =
                                                        currentRouteObjectPassStopListStations.getJSONObject(j)
                                                    passStopList.add(Pair(
                                                        currentRouteObjectPassStopListStationsObj.getString(
                                                            "stationName"),
                                                        "N"))
                                                }

                                                stationCount = currentRouteObjectPassStopListStations.length() - 1
                                            }
                                        }// 현재 수단이 지하철 인 case

                                    } // 마지막 수단 이전까지의 경로 수단을 다룸.

                                    // 마지막으로 해야 할일 -> 마지막 인덱스에 대한 일을 해야함 -> 무조건 목적지 까지 가기 위해서는 걸어야 하니까 그에 대한 걸 해야함
                                    else {
                                        // 마지막 인덱스
                                        var prevRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(i - 1)
                                        var currentRouteObject =
                                            minResultPathObjectSubPath.getJSONObject(i)
                                        if (prevRouteObject.getInt("trafficType") == 1) {
                                            // 이전 경로가 지하철일 경우. layout type은 5번
                                            layoutType = 5
                                            sectionTime = currentRouteObject.getInt("sectionTime")
                                            endName = prevRouteObject.getString("endName")
                                            startExitnoORendExitno =
                                                prevRouteObject.getString("endExitNo")
                                            distance = currentRouteObject.getInt("distance")
                                        } else {
                                            // 이전 경로가 버스일 경우
                                            layoutType = 7
                                            sectionTime = currentRouteObject.getInt("sectionTime")
                                            endName = prevRouteObject.getString("endName")
                                            distance = currentRouteObject.getInt("distance")
                                        }
                                    }
                                    // 이게 다음 반복문을 돌기 전에 list에 저장한거 넣어주고 모든 변수를 초기화해주는게 좋음.
                                    one_person_detail_route_array.add(
                                        RouteItemComponent(
                                            layoutType,
                                            trafficType,
                                            distance,
                                            sectionTime,
                                            stationCount,
                                            busNoORname,
                                            startName,
                                            endName,
                                            passStopList,
                                            door,
                                            startExitnoORendExitno,
                                            way
                                        )
                                    )

                                    layoutType = -1
                                    trafficType = -1
                                    distance = -1
                                    sectionTime = -1
                                    stationCount = -1
                                    busNoORname = "NoData"
                                    startName = "NoData"
                                    endName = "NoData"
                                    passStopList = arrayListOf()
                                    door = "NoData"
                                    startExitnoORendExitno = "NoData"
                                    way = "NoData"
                                } // 여기 까지 해서 모든 레이아웃 타입 완성.
                                //여기까지 하면 상세 보기에 들어갈 시간 요금 m 및 상세 경로 정보까지 다 들어감
                                //그럼 이제 할 일은 activity 실행 및 정보 넘기기를 해야함
                                one_person_detail_route_array.add(
                                    RouteItemComponent(9, trafficType, distance, sectionTime, stationCount, busNoORname, startName,endName,
                                        passStopList, door, startExitnoORendExitno, way)
                                )

                                Intent(context, OtherRouteToShowDetail::class.java).apply{
                                    putExtra("totalt", min_time)
                                    putExtra("totalw", min_distance)
                                    putExtra("totalp", min_payment)
                                    putParcelableArrayListExtra("totalr", ArrayList(one_person_detail_route_array))
                                }.run {context.startActivity(this)}
                            }
                        }catch(e: JSONException){
                          e.printStackTrace()
                        }
                    }

                    override fun onError(i: Int, errorMessage: String, api: API) {
                        Log.d("API 호출 실패","실패 했습니다.")
                    }
                }

            odsayService!!.requestSearchPubTransPath(
                sourceX,
                sourceY,
                desX,
                desY,
                0.toString(),
                0.toString(),
                0.toString(),
                onResultCallbackListener
            )
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