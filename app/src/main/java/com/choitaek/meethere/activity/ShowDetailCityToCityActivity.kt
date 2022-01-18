package com.choitaek.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.choitaek.meethere.R
import com.choitaek.meethere.adapter.DetailRouteAdapter
import com.choitaek.meethere.objects.ItemComponent
import com.choitaek.meethere.objects.RouteCityToCity
import com.choitaek.meethere.objects.RouteItemComponent
import com.choitaek.meethere.objects.SourceDestination
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_show_detail.*
import org.json.JSONException
import org.json.JSONObject

class ShowDetailCityToCityActivity : AppCompatActivity() {
    private var odsayService: ODsayService? = null
    private var jsonObject: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)

        total_time.setText("로딩 중입니다.")
        total_walk_time.setText("")
        total_fee.setText("")

        other_route.visibility = View.INVISIBLE

        var this_person_whole_route : MutableList<ItemComponent> = arrayListOf()
        var this_person_whole_detail_route : MutableList<MutableList<RouteItemComponent>> = arrayListOf()
        var city_to_city_index = 0
        var minimum_result_index = 0
        var this_person_total_walk_time : MutableList<Int> = arrayListOf()

        // 요 2개는 두 번째 콜백리스너에서 이어서 쓰기 때문에 onCreate에 써줌.

        odsayService = ODsayService.init(this, "hQVkqz/l8aEPCdgn6JlDWk793L3D/rl5Cyko3JqKhcw") // api가져옴.

        val intent = intent
        val Name = intent.getStringExtra("Name")

        var sotodes = intent.getParcelableExtra<SourceDestination>("sourceToDestination") as SourceDestination
        var routeCityTOCity = intent.getParcelableArrayListExtra<RouteCityToCity>("resultToDetailCityToCity") as ArrayList<RouteCityToCity>

        var onResultCallbackListener2: OnResultCallbackListener =
            object : OnResultCallbackListener {
                override fun onSuccess(ODsayData: ODsayData, api: API) {
                    try {
                        if(api == API.SEARCH_PUB_TRANS_PATH) {
                            Log.d("목적지 시외 -> 목적지 api호출(대중교통 타야하는 경우)","ㅇ")
                            var tempPathType = this_person_whole_route[city_to_city_index].pathType

                            //1. 시간이 제일 작은 경로 고르기
                            var result = ODsayData.json.getJSONObject("result")
                            var resultPath = result.getJSONArray("path")
                            var t = 9999999
                            var minimum_route_index = 0
                            var total_time_elem = 0
                            for(i in 0 until resultPath.length()){
                                var resultPathOBJ = resultPath.getJSONObject(i)
                                var resultPathOBJinfo = resultPathOBJ.getJSONObject("info")
                                if(resultPathOBJinfo.getInt("totalTime") < t){
                                    t = resultPathOBJinfo.getInt("totalTime")
                                    minimum_route_index = i
                                }
                            }
                            
                            var targetOBJ = resultPath.getJSONObject(minimum_route_index)
                            var targetOBJinfo = targetOBJ.getJSONObject("info")
                            if(targetOBJ.getInt("pathType") == 3) this_person_whole_route[city_to_city_index].pathType = 3
                            else{
                                if(tempPathType == 3) this_person_whole_route[city_to_city_index].pathType = 3
                                else if(tempPathType == 2){
                                    if(targetOBJ.getInt("pathType") == 2) this_person_whole_route[city_to_city_index].pathType = 2
                                    else this_person_whole_route[city_to_city_index].pathType = 3
                                }
                                else{
                                    if(targetOBJ.getInt("pathType") == 1) this_person_whole_route[city_to_city_index].pathType = 1
                                    else this_person_whole_route[city_to_city_index].pathType = 3
                                }
                            }

                            // 여기까지 해서 목적지 시외경로 -> 목적지 까지의 path type설정 및 최소 시간을 가진 object가져옴.
                            this_person_whole_route[city_to_city_index].totalTime += targetOBJinfo.getInt("totalTime")
                            // 시간 정보 추가.


                            var targetOBJsubPath = targetOBJ.getJSONArray("subPath")
                            for(i in 0 until targetOBJsubPath.length()){
                                var layoutType = -1
                                var trafficType = -1
                                var distance = -1
                                var sectionTime = -1
                                var stationCount = -1
                                var busNoORname = "NoData"
                                var startName = "NoData"
                                var endName = "NoData"
                                var passStopList: MutableList<Pair<String, String>> =
                                    arrayListOf()
                                var door = "NoData"
                                var startExitnoORendExitno = "NoData"
                                var way = "NoData"


                                // 기존 꺼 그대로 진행.
                                if(i == 0){
                                    var currentRouteObject = targetOBJsubPath.getJSONObject(0)
                                    var nextRouteObject = targetOBJsubPath.getJSONObject(1)

                                    this_person_whole_route[city_to_city_index].totalTimeTable.add(Pair(3, currentRouteObject.getInt("sectionTime")))
                                    this_person_whole_route[city_to_city_index].walkTime += currentRouteObject.getInt("sectionTime")

                                    if(nextRouteObject.getInt("trafficType") == 2){
                                        layoutType = 7
                                        endName = routeCityTOCity[city_to_city_index].endSTN
                                        startName = nextRouteObject.getString("startName")
                                        sectionTime = currentRouteObject.getInt("sectionTime")
                                        distance = currentRouteObject.getInt("distance")
                                    }else{
                                        layoutType = 7
                                        sectionTime = currentRouteObject.getInt("sectionTime")
                                        distance = currentRouteObject.getInt("distance")
                                        startName = nextRouteObject.getString("startName")
                                        endName = routeCityTOCity[city_to_city_index].endSTN
                                        if(nextRouteObject.optString("startExitNo") == ""){
                                            Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                            startExitnoORendExitno = "-1"
                                        }
                                        else{
                                            startExitnoORendExitno =
                                                nextRouteObject.getString("startExitNo")
                                        }
                                    }
                                } // i가 0일 때

                                else if(i < targetOBJsubPath.length() - 1){
                                    var prevRouteObject = targetOBJsubPath.getJSONObject(i-1)
                                    var currentRouteObject = targetOBJsubPath.getJSONObject(i)
                                    var nextRouteObject = targetOBJsubPath.getJSONObject(i+1)

                                    if(currentRouteObject.getInt("trafficType") == 3){
                                        this_person_whole_route[city_to_city_index].totalTimeTable.add(Pair(3, currentRouteObject.getInt("sectionTime")))
                                        this_person_whole_route[city_to_city_index].walkTime += currentRouteObject.getInt("sectionTime")

                                        if(prevRouteObject.getInt("trafficType") == 2){
                                            if(currentRouteObject.getInt("sectionTime")==0){
                                                layoutType = 8
                                                endName = prevRouteObject.getString("endName")
                                            }else{
                                                if(nextRouteObject.getInt("trafficType") == 1) {
                                                    layoutType = 7
                                                    sectionTime =
                                                        currentRouteObject.getInt("sectionTime")
                                                    endName =
                                                        prevRouteObject.getString("endName")
                                                    startName =
                                                        nextRouteObject.getString("startName")
                                                    if(nextRouteObject.optString("startExitNo") == ""){
                                                        Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                                        startExitnoORendExitno = "-1"
                                                    }
                                                    else{
                                                        startExitnoORendExitno =
                                                            nextRouteObject.getString("startExitNo")
                                                    }
                                                    distance =
                                                        currentRouteObject.getInt("distance")
                                                }else{
                                                    layoutType = 7
                                                    sectionTime =
                                                        currentRouteObject.getInt("sectionTime")
                                                    startName =
                                                        nextRouteObject.getString("startName")
                                                    endName =
                                                        prevRouteObject.getString("endName")
                                                    distance =
                                                        currentRouteObject.getInt("distance")
                                                }
                                            }
                                        }else{
                                            // 이전 경로가 지하철일 경우.
                                            if (prevRouteObject.getString("door") == "null") {
                                                // 이전에 지하철 타고 왔는데 환승이 아니라 그냥 내릴 경우.
                                                layoutType = 5
                                                sectionTime =
                                                    currentRouteObject.getInt("sectionTime")
                                                startName =
                                                    nextRouteObject.getString("startName")
                                                endName =
                                                    prevRouteObject.getString("endName")
                                                if(prevRouteObject.optString("endExitNo") == ""){
                                                    Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                                    startExitnoORendExitno = "-1"
                                                }
                                                else{
                                                    startExitnoORendExitno =
                                                        prevRouteObject.getString("endExitNo")
                                                }
                                                distance =
                                                    currentRouteObject.getInt("distance")
                                            } else {
                                                // 이전에 지하철 탔는데 이번에 환승할 경우
                                                layoutType = 4
                                                endName =
                                                    prevRouteObject.getString("endName")
                                            }
                                        }
                                    } else if(currentRouteObject.getInt("trafficType") == 2){
                                        layoutType = 3
                                        sectionTime =
                                            currentRouteObject.getInt("sectionTime")
                                        startName =
                                            currentRouteObject.getString("startName")
                                        var wholeRouteEndName =
                                            currentRouteObject.getString("endName")

                                        val currentRouteObjectLane =
                                            currentRouteObject.getJSONArray("lane")
                                        val currentRouteObjectLaneFirstInfo =
                                            currentRouteObjectLane.getJSONObject(0)
                                        busNoORname =
                                            currentRouteObjectLaneFirstInfo.getString("busNo")
                                        Log.d("각 케이스에서의 역 개수가 알고 싶어용",
                                            currentRouteObject.getInt("stationCount")
                                                .toString())

                                        // 먼저 전체 경로 처리
                                        this_person_whole_route[city_to_city_index].totalTimeTable.add(
                                            Pair(
                                                2, sectionTime
                                            )
                                        )
                                        this_person_whole_route[city_to_city_index].routesInfo.add(busNoORname)
                                        this_person_whole_route[city_to_city_index].routesInfo.add(startName)
                                        this_person_whole_route[city_to_city_index].routesInfo.add("승차")
                                        this_person_whole_route[city_to_city_index].routesInfo.add(busNoORname)
                                        this_person_whole_route[city_to_city_index].routesInfo.add(wholeRouteEndName)
                                        this_person_whole_route[city_to_city_index].routesInfo.add("하차")

                                        val currentRouteObjectPassStopList =
                                            currentRouteObject.getJSONObject("passStopList")
                                        val currentRouteObjectPassStopListStations =
                                            currentRouteObjectPassStopList.getJSONArray("stations")
                                        for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                            // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자.
                                            val currentRouteObjectPassStopListStationsObj =
                                                currentRouteObjectPassStopListStations.getJSONObject(
                                                    j)
                                            passStopList.add(Pair(
                                                currentRouteObjectPassStopListStationsObj.getString(
                                                    "stationName"),
                                                currentRouteObjectPassStopListStationsObj.getString(
                                                    "isNonStop")))
                                        }
                                        stationCount =
                                            currentRouteObjectPassStopListStations.length() - 1
                                    }else{
                                        if (nextRouteObject.getInt("sectionTime") != 0) {
                                            // 환승에 대한 정보가 없을 경우 -> null 이 때는 layout type이 2번
                                            layoutType = 2
                                            sectionTime =
                                                currentRouteObject.getInt("sectionTime")

                                            val currentRouteObjectLane =
                                                currentRouteObject.getJSONArray("lane")
                                            val currentRouteObjectLaneFirstInfo =
                                                currentRouteObjectLane.getJSONObject(0)
                                            busNoORname =
                                                currentRouteObjectLaneFirstInfo.getString("name")

                                            var wholeRouteEndName =
                                                currentRouteObject.getString("endName")
                                            startName =
                                                currentRouteObject.getString("startName")

                                            // 먼저 전체 경로 처리
                                            this_person_whole_route[city_to_city_index].totalTimeTable.add(
                                                Pair(
                                                    1, sectionTime
                                                )
                                            )
                                            this_person_whole_route[city_to_city_index].routesInfo.add(busNoORname)
                                            this_person_whole_route[city_to_city_index].routesInfo.add(startName)
                                            this_person_whole_route[city_to_city_index].routesInfo.add("승차")
                                            this_person_whole_route[city_to_city_index].routesInfo.add(busNoORname)
                                            this_person_whole_route[city_to_city_index].routesInfo.add(wholeRouteEndName)
                                            this_person_whole_route[city_to_city_index].routesInfo.add("하차")

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
                                            stationCount =
                                                currentRouteObjectPassStopListStations.length() - 1
                                        }else{
                                            // 환승에 대한 정보가 존재할 경우
                                            layoutType = 1
                                            sectionTime =
                                                currentRouteObject.getInt("sectionTime")

                                            val currentRouteObjectLane =
                                                currentRouteObject.getJSONArray("lane")
                                            val currentRouteObjectLaneFirstInfo =
                                                currentRouteObjectLane.getJSONObject(0)
                                            busNoORname =
                                                currentRouteObjectLaneFirstInfo.getString("name")

                                            var wholeRouteEndName =
                                                currentRouteObject.getString("endName")
                                            startName =
                                                currentRouteObject.getString("startName")

                                            // 먼저 전체 경로 처리
                                            this_person_whole_route[city_to_city_index].totalTimeTable.add(
                                                Pair(
                                                    1, sectionTime
                                                )
                                            )
                                            this_person_whole_route[city_to_city_index].routesInfo.add(busNoORname)
                                            this_person_whole_route[city_to_city_index].routesInfo.add(startName)
                                            this_person_whole_route[city_to_city_index].routesInfo.add("승차")
                                            this_person_whole_route[city_to_city_index].routesInfo.add(busNoORname)
                                            this_person_whole_route[city_to_city_index].routesInfo.add(wholeRouteEndName)
                                            this_person_whole_route[city_to_city_index].routesInfo.add("하차")

                                            way = currentRouteObject.getString("way")
                                            door = currentRouteObject.getString("door")

                                            val currentRouteObjectPassStopList =
                                                currentRouteObject.getJSONObject("passStopList")
                                            val currentRouteObjectPassStopListStations =
                                                currentRouteObjectPassStopList.getJSONArray(
                                                    "stations")
                                            for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                                // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자. 하지만 유의할 건 지하철이므로 미정차 여부는 N으로 채움,
                                                val currentRouteObjectPassStopListStationsObj =
                                                    currentRouteObjectPassStopListStations.getJSONObject(j)
                                                passStopList.add(Pair(
                                                    currentRouteObjectPassStopListStationsObj.getString(
                                                        "stationName"),
                                                    "N"))
                                            }

                                            stationCount =
                                                currentRouteObjectPassStopListStations.length() - 1
                                        }
                                    }
                                }
                                else{
                                    // 마지막 인덱스
                                    var prevRouteObject = targetOBJsubPath.getJSONObject(i - 1)
                                    var currentRouteObject = targetOBJsubPath.getJSONObject(i)

                                    this_person_whole_route[city_to_city_index].totalTimeTable.add(
                                        Pair(
                                            3, currentRouteObject.getInt("sectionTime")
                                        )
                                    )

                                    this_person_whole_route[city_to_city_index].walkTime += currentRouteObject.getInt("sectionTime")
                                    if (prevRouteObject.getInt("trafficType") == 1) {
                                        // 이전 경로가 지하철일 경우. layout type은 5번
                                        layoutType = 5
                                        sectionTime =
                                            currentRouteObject.getInt("sectionTime")
                                        endName = prevRouteObject.getString("endName")
                                        if(prevRouteObject.optString("endExitNo") == ""){
                                            Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                            startExitnoORendExitno = "-1"
                                        }
                                        else{
                                            startExitnoORendExitno =
                                                prevRouteObject.getString("endExitNo")
                                        }
                                        distance = currentRouteObject.getInt("distance")
                                    } else {
                                        // 이전 경로가 버스일 경우
                                        layoutType = 7
                                        sectionTime =
                                            currentRouteObject.getInt("sectionTime")
                                        endName = prevRouteObject.getString("endName")
                                        distance = currentRouteObject.getInt("distance")
                                    }
                                }

                                // 한 번 돌 때마다 경로의 부분 상세 경로 원소들은 완성, 전체 경로는 계속 추가 되는 방식.
                                this_person_whole_detail_route[city_to_city_index].add(
                                    RouteItemComponent(
                                        -1,
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
                            }// sub path for문 end.

                            this_person_whole_detail_route[city_to_city_index].add(
                                RouteItemComponent(
                                    -1,
                                    9,
                                    -1,
                                    -1,
                                    -1,
                                    -1,
                                    "NoData",
                                    "NoData",
                                    "NoData",
                                    arrayListOf(),
                                    "NoData",
                                    "NoData",
                                    "NoData",
                                )
                            )

                            Log.d("목적지까지의 데이터가 잘 들어갔나?", "ㅇㅇ")
                            Log.d("현재 인덱스", city_to_city_index.toString())

                            if(city_to_city_index == routeCityTOCity.size-1){
                                Log.d("다 들어감", "ㅇ")
                                Log.d("시외경로 개수", routeCityTOCity.size.toString())
                                Log.d("전체 루트 개수", this_person_whole_route.size.toString())
                                Log.d("전체 상세 루트 개수", this_person_whole_detail_route.size.toString())

                                var tt = 99999999

                                for(i in 0 until this_person_whole_route.size){
                                    if(tt > this_person_whole_route[i].totalTime){
                                        minimum_result_index = i
                                        tt = this_person_whole_route[i].totalTime
                                    }
                                }

                                if(tt > 60){
                                    total_time.setText(""+(tt / 60)+"시간 "+(tt % 60)+"분")
                                }
                                else{
                                    total_time.setText(""+tt+"분")
                                }
                                total_walk_time.setText("도보 "+this_person_whole_route[minimum_result_index].walkTime+"분")
                                total_fee.setText("")

                                other_route.visibility = View.VISIBLE

                                val adapter = DetailRouteAdapter(this_person_whole_detail_route, this_person_whole_route, minimum_result_index)
                                recycler_view.adapter = adapter
                            }
                            Log.d("before city index", city_to_city_index.toString())
                            city_to_city_index++
                            Log.d("after city index", city_to_city_index.toString())
                        }
                    }catch(e: JSONException){

                    }
                }

                override fun onError(i: Int, errorMessage: String, api: API) {
                    Log.d("목적지 시외 -> 목적지 api호출(걸어가야 하는 경우)","ㅇ")
                    this_person_whole_route[city_to_city_index].totalTimeTable.add(
                        Pair(3, 0)

                    )

                    this_person_whole_detail_route[city_to_city_index].add(
                        RouteItemComponent(
                            -2,
                            7,
                            0,
                            0,
                            0,
                            0,
                            "NoData",
                            routeCityTOCity[city_to_city_index].endSTN,
                            "NoData",
                            arrayListOf(),
                            "NoData",
                            "NoData",
                            "NoData"
                        )
                    )

                    this_person_whole_detail_route[city_to_city_index].add(
                        RouteItemComponent(
                            -1,
                            9,
                            -1,
                            -1,
                            -1,
                            -1,
                            "NoData",
                            "NoData",
                            "NoData",
                            arrayListOf(),
                            "NoData",
                            "NoData",
                            "NoData",
                        )
                    )
                    // 이 떄는?

                    Log.d("목적지까지의 데이터가 잘 들어갔나?", "ㅇㅇ")
                    Log.d("현재 인덱스", city_to_city_index.toString())

                    if(city_to_city_index == routeCityTOCity.size-1){
                        Log.d("다 들어감", "ㅇ")
                        Log.d("시외경로 개수", routeCityTOCity.size.toString())
                        Log.d("전체 루트 개수", this_person_whole_route.size.toString())
                        Log.d("전체 상세 루트 개수", this_person_whole_detail_route.size.toString())

                        var tt = 99999999

                        for(i in 0 until this_person_whole_route.size){
                            if(tt > this_person_whole_route[i].totalTime){
                                minimum_result_index = i
                                tt = this_person_whole_route[i].totalTime
                            }
                        }

                        if(tt > 60){
                            total_time.setText(""+(tt / 60)+"시간 "+(tt % 60)+"분")
                        }
                        else{
                            total_time.setText(""+tt+"분")
                        }
                        total_walk_time.setText("도보 "+this_person_whole_route[minimum_result_index].walkTime+"분")
                        total_fee.setText("")

                        val adapter = DetailRouteAdapter(this_person_whole_detail_route, this_person_whole_route, minimum_result_index)
                        recycler_view.adapter = adapter
                    }

                    city_to_city_index++


                }

            }

        var onResultCallbackListener1: OnResultCallbackListener =
            object : OnResultCallbackListener {
                override fun onSuccess(ODsayData: ODsayData, api: API) {
                    try {
                        if(api == API.SEARCH_PUB_TRANS_PATH) {
                            var this_person_whole_detail_route_elem : MutableList<RouteItemComponent> = arrayListOf()
                            var routesInfo: MutableList<String> = arrayListOf()
                            var totalTimeTable: MutableList<Pair<Int, Int>> = arrayListOf()
                            Log.d("출발지 -> 출발지 시외 api호출(대중교통 타야하는 경우)","ㅇ")

                            //1. 시간이 제일 작은 경로 고르기
                            var result = ODsayData.json.getJSONObject("result")
                            var resultPath = result.getJSONArray("path")
                            var t = 9999999
                            var minimum_route_index = 0
                            var total_time_elem = 0
                            for(i in 0 until resultPath.length()){
                                var resultPathOBJ = resultPath.getJSONObject(i)
                                var resultPathOBJinfo = resultPathOBJ.getJSONObject("info")
                                if(resultPathOBJinfo.getInt("totalTime") < t){
                                    t = resultPathOBJinfo.getInt("totalTime")
                                    minimum_route_index = i
                                }
                            }

                            var targetOBJ = resultPath.getJSONObject(minimum_route_index)
                            // targetOBJ에서 알 수 있는거 ->path Type
                            var targetOBJinfo = targetOBJ.getJSONObject("info")
                            // targetOBJinfo에서 알 수 있는거 -> total time,

                            var targetOBJsubPath = targetOBJ.getJSONArray("subPath")

                            for(i in 0 until targetOBJsubPath.length()){
                                var layoutType = -1
                                var trafficType = -1
                                var distance = -1
                                var sectionTime = -1
                                var stationCount = -1
                                var busNoORname = "NoData"
                                var startName = "NoData"
                                var endName = "NoData"
                                var passStopList: MutableList<Pair<String, String>> =
                                    arrayListOf()
                                var door = "NoData"
                                var startExitnoORendExitno = "NoData"
                                var way = "NoData"


                                // 기존 꺼 그대로 진행.
                                if(i == 0){
                                    var currentRouteObject = targetOBJsubPath.getJSONObject(0)
                                    var nextRouteObject = targetOBJsubPath.getJSONObject(1)

                                    totalTimeTable.add(Pair(3, currentRouteObject.getInt("sectionTime")))
                                    total_time_elem += currentRouteObject.getInt("sectionTime")

                                    if(nextRouteObject.getInt("trafficType") == 2){
                                        layoutType = 6
                                        sectionTime = currentRouteObject.getInt("sectionTime")
                                        distance = currentRouteObject.getInt("distance")
                                        startName = nextRouteObject.getString("startName")
                                    }else{
                                        layoutType = 6
                                        sectionTime = currentRouteObject.getInt("sectionTime")
                                        distance = currentRouteObject.getInt("distance")
                                        startName = nextRouteObject.getString("startName")
                                        if(nextRouteObject.optString("startExitNo") == ""){
                                            Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                            startExitnoORendExitno = "-1"
                                        }
                                        else{
                                            startExitnoORendExitno =
                                                nextRouteObject.getString("startExitNo")
                                        }
                                    }
                                } // i가 0일 때

                                else if(i < targetOBJsubPath.length() - 1){
                                    var prevRouteObject = targetOBJsubPath.getJSONObject(i-1)
                                    var currentRouteObject = targetOBJsubPath.getJSONObject(i)
                                    var nextRouteObject = targetOBJsubPath.getJSONObject(i+1)

                                    if(currentRouteObject.getInt("trafficType") == 3){
                                        totalTimeTable.add(Pair(3, currentRouteObject.getInt("sectionTime")))
                                        total_time_elem += currentRouteObject.getInt("sectionTime")

                                        if(prevRouteObject.getInt("trafficType") == 2){
                                            if(currentRouteObject.getInt("sectionTime")==0){
                                                layoutType = 8
                                                endName = prevRouteObject.getString("endName")
                                            }else{
                                                if(nextRouteObject.getInt("trafficType") == 1) {
                                                    layoutType = 7
                                                    sectionTime =
                                                        currentRouteObject.getInt("sectionTime")
                                                    endName =
                                                        prevRouteObject.getString("endName")
                                                    startName =
                                                        nextRouteObject.getString("startName")
                                                    if(nextRouteObject.optString("startExitNo") == ""){
                                                        Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                                        startExitnoORendExitno = "-1"
                                                    }
                                                    else{
                                                        startExitnoORendExitno =
                                                            nextRouteObject.getString("startExitNo")
                                                    }
                                                    distance =
                                                        currentRouteObject.getInt("distance")
                                                }else{
                                                    layoutType = 7
                                                    startName =
                                                        nextRouteObject.getString("startName")
                                                    sectionTime =
                                                        currentRouteObject.getInt("sectionTime")
                                                    endName =
                                                        prevRouteObject.getString("endName")
                                                    distance =
                                                        currentRouteObject.getInt("distance")
                                                }
                                            }
                                        }else{
                                            // 이전 경로가 지하철일 경우.
                                            if (prevRouteObject.getString("door") == "null") {
                                                // 이전에 지하철 타고 왔는데 환승이 아니라 그냥 내릴 경우.
                                                layoutType = 5
                                                sectionTime =
                                                    currentRouteObject.getInt("sectionTime")
                                                startName =
                                                    currentRouteObject.getString("startName")
                                                endName =
                                                    prevRouteObject.getString("endName")
                                                if(prevRouteObject.optString("endExitNo") == ""){
                                                    Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                                    startExitnoORendExitno = "-1"
                                                }
                                                else{
                                                    startExitnoORendExitno =
                                                        prevRouteObject.getString("endExitNo")
                                                }
                                                distance =
                                                    currentRouteObject.getInt("distance")
                                            } else {
                                                // 이전에 지하철 탔는데 이번에 환승할 경우
                                                layoutType = 4
                                                endName =
                                                    prevRouteObject.getString("endName")
                                            }
                                        }
                                    } else if(currentRouteObject.getInt("trafficType") == 2){
                                        layoutType = 3
                                        sectionTime =
                                            currentRouteObject.getInt("sectionTime")
                                        startName =
                                            currentRouteObject.getString("startName")
                                        var wholeRouteEndName =
                                            currentRouteObject.getString("endName")

                                        val currentRouteObjectLane =
                                            currentRouteObject.getJSONArray("lane")
                                        val currentRouteObjectLaneFirstInfo =
                                            currentRouteObjectLane.getJSONObject(0)
                                        busNoORname =
                                            currentRouteObjectLaneFirstInfo.getString("busNo")
                                        Log.d("각 케이스에서의 역 개수가 알고 싶어용",
                                            currentRouteObject.getInt("stationCount")
                                                .toString())

                                        // 먼저 전체 경로 처리
                                        totalTimeTable.add(
                                            Pair(
                                                2, sectionTime
                                            )
                                        )
                                        routesInfo.add(busNoORname)
                                        routesInfo.add(startName)
                                        routesInfo.add("승차")
                                        routesInfo.add(busNoORname)
                                        routesInfo.add(wholeRouteEndName)
                                        routesInfo.add("하차")

                                        val currentRouteObjectPassStopList =
                                            currentRouteObject.getJSONObject("passStopList")
                                        val currentRouteObjectPassStopListStations =
                                            currentRouteObjectPassStopList.getJSONArray("stations")
                                        for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                            // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자.
                                            val currentRouteObjectPassStopListStationsObj =
                                                currentRouteObjectPassStopListStations.getJSONObject(
                                                    j)
                                            passStopList.add(Pair(
                                                currentRouteObjectPassStopListStationsObj.getString(
                                                    "stationName"),
                                                currentRouteObjectPassStopListStationsObj.getString(
                                                    "isNonStop")))
                                        }
                                        stationCount =
                                            currentRouteObjectPassStopListStations.length() - 1
                                    }else{
                                        if (nextRouteObject.getInt("sectionTime") != 0) {
                                            // 환승에 대한 정보가 없을 경우 -> null 이 때는 layout type이 2번
                                            layoutType = 2
                                            sectionTime =
                                                currentRouteObject.getInt("sectionTime")

                                            val currentRouteObjectLane =
                                                currentRouteObject.getJSONArray("lane")
                                            val currentRouteObjectLaneFirstInfo =
                                                currentRouteObjectLane.getJSONObject(0)
                                            busNoORname =
                                                currentRouteObjectLaneFirstInfo.getString("name")

                                            var wholeRouteEndName =
                                                currentRouteObject.getString("endName")
                                            startName =
                                                currentRouteObject.getString("startName")

                                            // 먼저 전체 경로 처리
                                            totalTimeTable.add(
                                                Pair(
                                                    1, sectionTime
                                                )
                                            )
                                            routesInfo.add(busNoORname)
                                            routesInfo.add(startName)
                                            routesInfo.add("승차")
                                            routesInfo.add(busNoORname)
                                            routesInfo.add(wholeRouteEndName)
                                            routesInfo.add("하차")

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
                                            stationCount =
                                                currentRouteObjectPassStopListStations.length() - 1
                                        }else{
                                            // 환승에 대한 정보가 존재할 경우
                                            layoutType = 1
                                            sectionTime =
                                                currentRouteObject.getInt("sectionTime")

                                            val currentRouteObjectLane =
                                                currentRouteObject.getJSONArray("lane")
                                            val currentRouteObjectLaneFirstInfo =
                                                currentRouteObjectLane.getJSONObject(0)
                                            busNoORname =
                                                currentRouteObjectLaneFirstInfo.getString("name")

                                            var wholeRouteEndName =
                                                currentRouteObject.getString("endName")
                                            startName =
                                                currentRouteObject.getString("startName")

                                            // 먼저 전체 경로 처리
                                            totalTimeTable.add(
                                                Pair(
                                                    1, sectionTime
                                                )
                                            )
                                            routesInfo.add(busNoORname)
                                            routesInfo.add(startName)
                                            routesInfo.add("승차")
                                            routesInfo.add(busNoORname)
                                            routesInfo.add(wholeRouteEndName)
                                            routesInfo.add("하차")

                                            way = currentRouteObject.getString("way")
                                            door = currentRouteObject.getString("door")

                                            val currentRouteObjectPassStopList =
                                                currentRouteObject.getJSONObject("passStopList")
                                            val currentRouteObjectPassStopListStations =
                                                currentRouteObjectPassStopList.getJSONArray(
                                                    "stations")
                                            for (j in 0 until currentRouteObjectPassStopListStations.length()) {
                                                // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자. 하지만 유의할 건 지하철이므로 미정차 여부는 N으로 채움,
                                                val currentRouteObjectPassStopListStationsObj =
                                                    currentRouteObjectPassStopListStations.getJSONObject(j)
                                                passStopList.add(Pair(
                                                    currentRouteObjectPassStopListStationsObj.getString(
                                                        "stationName"),
                                                    "N"))
                                            }

                                            stationCount =
                                                currentRouteObjectPassStopListStations.length() - 1
                                        }
                                    }
                                }
                                else{
                                    // 마지막 인덱스
                                    var prevRouteObject = targetOBJsubPath.getJSONObject(i - 1)
                                    var currentRouteObject = targetOBJsubPath.getJSONObject(i)

                                    totalTimeTable.add(
                                        Pair(
                                            3, currentRouteObject.getInt("sectionTime")
                                        )
                                    )

                                    total_time_elem += currentRouteObject.getInt("sectionTime")
                                    if (prevRouteObject.getInt("trafficType") == 1) {
                                        // 이전 경로가 지하철일 경우. layout type은 5번
                                        layoutType = 5
                                        sectionTime =
                                            currentRouteObject.getInt("sectionTime")
                                        startName = routeCityTOCity[city_to_city_index].startSTN
                                        endName = prevRouteObject.getString("endName")
                                        if(prevRouteObject.optString("endExitNo") == ""){
                                            Log.d("도보->지하철인데 ","빠른 입구가 없는 경우")
                                            startExitnoORendExitno = "-1"
                                        }
                                        else{
                                            startExitnoORendExitno =
                                                prevRouteObject.getString("endExitNo")
                                        }
                                        distance = currentRouteObject.getInt("distance")
                                    } else {
                                        // 이전 경로가 버스일 경우
                                        layoutType = 7
                                        sectionTime =
                                            currentRouteObject.getInt("sectionTime")
                                        startName = routeCityTOCity[city_to_city_index].startSTN
                                        endName = prevRouteObject.getString("endName")
                                        distance = currentRouteObject.getInt("distance")
                                    }
                                }

                            // 한 번 돌 때마다 경로의 부분 상세 경로 원소들은 완성, 전체 경로는 계속 추가 되는 방식.
                                this_person_whole_detail_route_elem.add(
                                    RouteItemComponent(
                                        -1,
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
                            }// sub path for문 end.


                            // 나와서 할 일
                            // 1. 시외 경로 보고 필요한 곳에 끼워넣기.
                            // 시외 경로 적용되면서 값이 바뀌거나 추가 되는곳
                            // * totalTIme, routesInfo, totalTimeTable,  // * 상세 경로는 애초에 배열 요소가 하나 추가 됨.

                            
                            if(routeCityTOCity[city_to_city_index].busORtrainORair == 4){
                                // 지하철일 경우
                                t += routeCityTOCity[city_to_city_index].LaneInfo[0].second
                                routesInfo.add("열차")
                                routesInfo.add(routeCityTOCity[city_to_city_index].startSTN)
                                routesInfo.add("탑승")
                                routesInfo.add("열차")
                                routesInfo.add(routeCityTOCity[city_to_city_index].endSTN)
                                routesInfo.add("하차")

                                totalTimeTable.add(
                                    Pair(4,routeCityTOCity[city_to_city_index].LaneInfo[0].second)
                                )

                                var passlist :MutableList<Pair<String, String>> = arrayListOf()
                                for(i in 0 until routeCityTOCity[city_to_city_index].LaneInfo.size){
                                    passlist.add(Pair(routeCityTOCity[city_to_city_index].LaneInfo[i].first, routeCityTOCity[city_to_city_index].LaneInfo[i].second.toString()))
                                }

                                // 상세 경로 채우기
                                this_person_whole_detail_route_elem.add(
                                    RouteItemComponent(
                                        -1,
                                        11,
                                        4,
                                        -1,
                                        routeCityTOCity[city_to_city_index].LaneInfo[0].second,
                                        -1,
                                        "열차",
                                        routeCityTOCity[city_to_city_index].startSTN,
                                        routeCityTOCity[city_to_city_index].endSTN,
                                        passlist,
                                        "NoData",
                                        "NoData",
                                        "NoData"
                                    )
                                )
                            }else if(routeCityTOCity[city_to_city_index].busORtrainORair == 5 || routeCityTOCity[city_to_city_index].busORtrainORair == 6){
                                // 고속 버스 or 시외버스
                                t += routeCityTOCity[city_to_city_index].LaneInfo[0].second
                                routesInfo.add("버스")
                                routesInfo.add(routeCityTOCity[city_to_city_index].startSTN)
                                routesInfo.add("탑승")
                                routesInfo.add("버스")
                                routesInfo.add(routeCityTOCity[city_to_city_index].endSTN)
                                routesInfo.add("하차")

                                totalTimeTable.add(
                                    Pair(5,routeCityTOCity[city_to_city_index].LaneInfo[0].second)
                                )

                                var passlist :MutableList<Pair<String, String>> = arrayListOf()
                                for(i in 0 until routeCityTOCity[city_to_city_index].LaneInfo.size){
                                    passlist.add(Pair(routeCityTOCity[city_to_city_index].LaneInfo[i].first, routeCityTOCity[city_to_city_index].LaneInfo[i].second.toString()))
                                }

                                // 상세 경로 채우기
                                this_person_whole_detail_route_elem.add(
                                    RouteItemComponent(
                                        -1,
                                        10,
                                        5,
                                        -1,
                                        routeCityTOCity[city_to_city_index].LaneInfo[0].second,
                                        -1,
                                        "버스",
                                        routeCityTOCity[city_to_city_index].startSTN,
                                        routeCityTOCity[city_to_city_index].endSTN,
                                        passlist,
                                        "NoData",
                                        "NoData",
                                        "NoData"
                                    )
                                )
                            }else{
                                t += routeCityTOCity[city_to_city_index].LaneInfo[0].second
                                routesInfo.add("비행기")
                                routesInfo.add(routeCityTOCity[city_to_city_index].startSTN)
                                routesInfo.add("탑승")
                                routesInfo.add("비행기")
                                routesInfo.add(routeCityTOCity[city_to_city_index].endSTN)
                                routesInfo.add("하차")

                                totalTimeTable.add(
                                    Pair(6,routeCityTOCity[city_to_city_index].LaneInfo[0].second)
                                )

                                var passlist :MutableList<Pair<String, String>> = arrayListOf()
                                for(i in 0 until routeCityTOCity[city_to_city_index].LaneInfo.size){
                                    passlist.add(Pair(routeCityTOCity[city_to_city_index].LaneInfo[i].first, routeCityTOCity[city_to_city_index].LaneInfo[i].second.toString()))
                                }

                                // 상세 경로 채우기
                                this_person_whole_detail_route_elem.add(
                                    RouteItemComponent(
                                        -1,
                                        12,
                                        6,
                                        -1,
                                        routeCityTOCity[city_to_city_index].LaneInfo[0].second,
                                        -1,
                                        "비행기",
                                        routeCityTOCity[city_to_city_index].startSTN,
                                        routeCityTOCity[city_to_city_index].endSTN,
                                        passlist,
                                        "NoData",
                                        "NoData",
                                        "NoData"
                                    )
                                )
                            }

                            // 여기까지 해서 모든 정보를 채웠음.상세 경로는 이미 다 들어가있는 상태.
                            // 전체 경로 채우기.
                            this_person_whole_route.add(
                                ItemComponent(
                                    -1,
                                    targetOBJ.getInt("pathType"),
                                    t,
                                    total_time_elem,
                                    0,
                                    routesInfo,
                                    totalTimeTable
                                )
                            )

                            this_person_whole_detail_route.add(this_person_whole_detail_route_elem)
                            this_person_total_walk_time.add(total_time_elem)

                            Log.d("출발지->목적 시외", this_person_whole_route[city_to_city_index].toString())
                            Log.d("출발지->목적 시외 상세 경로", this_person_whole_detail_route[city_to_city_index].toString())

                            Log.d("시외경로 개수", routeCityTOCity.size.toString())
                            Log.d("city인덱스", city_to_city_index.toString())
                        }
                    }catch(e: JSONException){

                    }
                }

                override fun onError(i: Int, errorMessage: String, api: API) {
                    Log.d("출발지 -> 출발지 시외 api호출(걸어가야 하는 경우)","ㅇ")
                    // 출발지 -> 출발지 시외까지 대중교통이 아닌 700m 이내의 거리라서 걸어가야 하는 경우

                    var this_person_whole_detail_route_elem : MutableList<RouteItemComponent> = arrayListOf()
                    var routesInfo: MutableList<String> = arrayListOf()
                    var totalTimeTable: MutableList<Pair<Int, Int>> = arrayListOf()

                    var t = 0 // 총 시간
                    var pathType = 0
                    var elem = 0 // 걷는 시간
                    var fee = 0 // 요금
                    // routesInfo에는 들어가는게 없지. 왜냐 걸어가는거니까 따로 탈 수단이 없음

                    totalTimeTable.add(
                        Pair(3, 0)
                    )

                    this_person_whole_detail_route_elem.add(
                        RouteItemComponent(
                            -2,
                            6,
                            0,
                            0,
                            0,
                            0,
                            "NoData",
                            "NoData",
                            routeCityTOCity[city_to_city_index].startSTN,
                            arrayListOf(),
                            "NoData",
                            "NoData",
                            "NoData"
                        )
                    )

                    if(routeCityTOCity[city_to_city_index].busORtrainORair == 4){
                        // 지하철일 경우
                        t += routeCityTOCity[city_to_city_index].LaneInfo[0].second
                        routesInfo.add("열차")
                        routesInfo.add(routeCityTOCity[city_to_city_index].startSTN)
                        routesInfo.add("탑승")
                        routesInfo.add("열차")
                        routesInfo.add(routeCityTOCity[city_to_city_index].endSTN)
                        routesInfo.add("하차")

                        totalTimeTable.add(
                            Pair(4,routeCityTOCity[city_to_city_index].LaneInfo[0].second)
                        )

                        var passlist :MutableList<Pair<String, String>> = arrayListOf()
                        for(i in 0 until routeCityTOCity[city_to_city_index].LaneInfo.size){
                            passlist.add(Pair(routeCityTOCity[city_to_city_index].LaneInfo[i].first, routeCityTOCity[city_to_city_index].LaneInfo[i].second.toString()))
                        }

                        // 상세 경로 채우기
                        this_person_whole_detail_route_elem.add(
                            RouteItemComponent(
                                -1,
                                11,
                                4,
                                -1,
                                routeCityTOCity[city_to_city_index].LaneInfo[0].second,
                                -1,
                                "열차",
                                routeCityTOCity[city_to_city_index].startSTN,
                                routeCityTOCity[city_to_city_index].endSTN,
                                passlist,
                                "NoData",
                                "NoData",
                                "NoData"
                            )
                        )
                    }else if(routeCityTOCity[city_to_city_index].busORtrainORair == 5 || routeCityTOCity[city_to_city_index].busORtrainORair == 6){
                        // 고속 버스 or 시외버스
                        t += routeCityTOCity[city_to_city_index].LaneInfo[0].second
                        routesInfo.add("버스")
                        routesInfo.add(routeCityTOCity[city_to_city_index].startSTN)
                        routesInfo.add("탑승")
                        routesInfo.add("버스")
                        routesInfo.add(routeCityTOCity[city_to_city_index].endSTN)
                        routesInfo.add("하차")

                        totalTimeTable.add(
                            Pair(5,routeCityTOCity[city_to_city_index].LaneInfo[0].second)
                        )

                        var passlist :MutableList<Pair<String, String>> = arrayListOf()
                        for(i in 0 until routeCityTOCity[city_to_city_index].LaneInfo.size){
                            passlist.add(Pair(routeCityTOCity[city_to_city_index].LaneInfo[i].first, routeCityTOCity[city_to_city_index].LaneInfo[i].second.toString()))
                        }

                        // 상세 경로 채우기
                        this_person_whole_detail_route_elem.add(
                            RouteItemComponent(
                                -1,
                                10,
                                5,
                                -1,
                                routeCityTOCity[city_to_city_index].LaneInfo[0].second,
                                -1,
                                "버스",
                                routeCityTOCity[city_to_city_index].startSTN,
                                routeCityTOCity[city_to_city_index].endSTN,
                                passlist,
                                "NoData",
                                "NoData",
                                "NoData"
                            )
                        )
                    }else{
                        t += routeCityTOCity[city_to_city_index].LaneInfo[0].second
                        routesInfo.add("비행기")
                        routesInfo.add(routeCityTOCity[city_to_city_index].startSTN)
                        routesInfo.add("탑승")
                        routesInfo.add("비행기")
                        routesInfo.add(routeCityTOCity[city_to_city_index].endSTN)
                        routesInfo.add("하차")

                        totalTimeTable.add(
                            Pair(6,routeCityTOCity[city_to_city_index].LaneInfo[0].second)
                        )

                        var passlist :MutableList<Pair<String, String>> = arrayListOf()
                        for(i in 0 until routeCityTOCity[city_to_city_index].LaneInfo.size){
                            passlist.add(Pair(routeCityTOCity[city_to_city_index].LaneInfo[i].first, routeCityTOCity[city_to_city_index].LaneInfo[i].second.toString()))
                        }

                        // 상세 경로 채우기
                        this_person_whole_detail_route_elem.add(
                            RouteItemComponent(
                                -1,
                                12,
                                6,
                                -1,
                                routeCityTOCity[city_to_city_index].LaneInfo[0].second,
                                -1,
                                "비행기",
                                routeCityTOCity[city_to_city_index].startSTN,
                                routeCityTOCity[city_to_city_index].endSTN,
                                passlist,
                                "NoData",
                                "NoData",
                                "NoData"
                            )
                        )
                    }


                    this_person_whole_route.add(
                        ItemComponent(
                            -1,
                            0,
                            t,
                            0,
                            0,
                            routesInfo,
                            totalTimeTable
                        )
                    )

                    this_person_whole_detail_route.add(this_person_whole_detail_route_elem)
                    this_person_total_walk_time.add(elem)

                    Log.d("출발지->목적 시외(출발지에서 도보)", this_person_whole_route[city_to_city_index].toString())
                    Log.d("출발지->목적 시외 상세 경로(도보)", this_person_whole_detail_route[city_to_city_index].toString())

                }

            }
        for(i in 0 until routeCityTOCity.size){
            odsayService!!.requestSearchPubTransPath(
                sotodes.sx.toString(),
                sotodes.sy.toString(),
                routeCityTOCity[i].SX.toString(),
                routeCityTOCity[i].SY.toString(),
                0.toString(),
                0.toString(),
                0.toString(),
                onResultCallbackListener1
            )

            odsayService!!.requestSearchPubTransPath(
                routeCityTOCity[i].EX.toString(),
                routeCityTOCity[i].EY.toString(),
                sotodes.dx.toString(),
                sotodes.dy.toString(),
                0.toString(),
                0.toString(),
                0.toString(),
                onResultCallbackListener2
            )
        }


        val toolbar = supportActionBar
        toolbar!!.title="상세보기"

        toolbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setDisplayHomeAsUpEnabled(true)


        other_route.setOnClickListener {
            // API 다시 호출.
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            // 여기서는 이제 최소 시간을 줄 필요는 없음 이제부터

            val intent = Intent(this, OtherRouteCityToCityActivity::class.java)
            intent.putExtra("CityToCityDetailToOtherWholeRoute", ArrayList(this_person_whole_route))
            intent.putExtra("CityToCityDetailToOtherDetailRoute", ArrayList(this_person_whole_detail_route))
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}