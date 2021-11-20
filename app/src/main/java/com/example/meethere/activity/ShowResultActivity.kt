package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_show_result.*
import kotlinx.android.synthetic.main.item_result.*
import android.content.ClipData
import android.content.ClipboardManager
import android.util.Log
import android.view.MenuItem
import com.example.meethere.R
import com.example.meethere.adapter.ResultAdapter
import com.example.meethere.objects.*
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.retrofit.request.Share
import com.example.meethere.retrofit.request.ShareAddress
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import org.json.JSONObject
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONException

class ShowResultActivity : AppCompatActivity() {
    private lateinit var resultAdapter: ResultAdapter
    private var odsayService: ODsayService? = null
    private var jsonObject: JSONObject? = null

    private var names : MutableList<String> = arrayListOf()
    var loop_i : Int = 0

    private fun startToDetailActivity(id: Int, message: String) {
        val intent = Intent(applicationContext, ShowDetailActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result)

        odsayService = ODsayService.init(this, "hQVkqz/l8aEPCdgn6JlDWk793L3D/rl5Cyko3JqKhcw") // api가져옴.
        Log.d("asdfr확인 ", "->확인")

        //출발 주소 리스트
        val addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("addressData") as Array<AddressObject>

        //도착 주소
        val addressObject: AddressObject =
            intent.getSerializableExtra("addressObject") as AddressObject
        val destinationName: String? = addressObject.place_name


        var onResultCallbackListener: OnResultCallbackListener =
            object : OnResultCallbackListener {
                // 호출성공시 onSuccess -> 이 안에서 뭘 하냐
                // 호출을 성공했을 때 fragment에 data를 뿌려주고 싶은데....................
                // API호출 결과 데이터 리턴.
                override fun onSuccess(ODsayData: ODsayData, api: API) {
                    Log.d("API호출 성공", "성공")
                    jsonObject = ODsayData.json
                    try {
                        if(api == API.SEARCH_PUB_TRANS_PATH) {
                            // 이 안에서 할 일. 먼저 전체 경로가 몇 개일까여?
                            // busCount + subwayCount + subwayBusCount를 더한 것이 dynamic button의 개수
                            var every_person_detail_route_array: MutableList<MutableList<RouteItemComponent>> =
                                arrayListOf()
                            var whole_route_info : MutableList<ItemComponent> =
                                arrayListOf()
                            var result = ODsayData.json.getJSONObject("result")
                            var resultPath = result.getJSONArray("path")
                            var min_time: Int = 999999999
                            var min_index: Int = 0
                            var loop_init : Int = 0

                            for(i in 0 until resultPath.length()){
                                var routeItemComponentElement : MutableList<RouteItemComponent> = arrayListOf()
                                // routeItemComponentElement는 every person detail route array에 들어가는 각각의 배열.

                                var resultPathObj = resultPath.getJSONObject(i)
                                var resultPathObjInfo = resultPathObj.getJSONObject("info")
                                var resultPathObjSubPath = resultPathObj.getJSONArray("subPath")
                                var pathType = resultPathObj.getInt("pathType")
                                var totalTime = resultPathObjInfo.getInt("totalTime")
                                var totalFee = resultPathObjInfo.getInt("payment")
                                // subpath에 들어가기 전에 구할 수 있는 정보는 다 구함

                                if(min_time > totalTime) {
                                    Log.d("최소 시간 발견!", totalTime.toString())
                                    Log.d("최소 시간 인덱스!", loop_init.toString())
                                    min_time = totalTime
                                    min_index = loop_init
                                }



                                // 이제부터 sub path진입.
                                var tempMinWalkTime : Int = 0
                                var routesInfo: MutableList<String> = arrayListOf()
                                var totalTimeTable: MutableList<Pair<Int, Int>> = arrayListOf()
                                // 요기는 전체 경로에 들어가는 거.

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
                                // 요기는 상세 경로에 들어갈 얘들.
                                for(j in 0 until resultPathObjSubPath.length()) {
                                    if(j == 0){
                                        var currentRouteObject =  resultPathObjSubPath.getJSONObject(0)
                                        var tempNextRouteObject = resultPathObjSubPath.getJSONObject(1)

                                        // 먼저 전체 경로에 대한 처리 먼저 -> 첫번째 인덱스는 무조건 도보이므로 해당되는 부분은 total time table
                                        totalTimeTable.add(
                                            Pair(
                                                3, currentRouteObject.getInt("sectionTime")
                                            )
                                        )

                                        tempMinWalkTime += currentRouteObject.getInt("sectionTime")


                                        // 다음으로 상세 경로에 대한 정보 처리.
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
                                    } // j가 0일 때

                                    else if (j < resultPathObjSubPath.length() - 1) {
                                        // 나머지 인덱스에 대한 case(마지막 인덱스에 대한 case는 도보이므로 제외)

                                        var prevRouteObject =
                                            resultPathObjSubPath.getJSONObject(j - 1)
                                        var currentRouteObject =
                                            resultPathObjSubPath.getJSONObject(j)
                                        var nextRouteObject =
                                            resultPathObjSubPath.getJSONObject(j + 1)
                                        // 그럼 이 위에서는 전체 경로에 대한 처리를 해야겠죠?


                                        // 요기서부터는 상세 경로에 대한 case를 처리하겠지?
                                        if (currentRouteObject.getInt("trafficType") == 3) {
                                            // 현재가 도보일 경우. 전체 경로에 대한 case에서는 어떻게 해야할까
                                            // walk time, totalTimeTable의 정보가 필요하겠죠?

                                            totalTimeTable.add(
                                                Pair(
                                                    3, currentRouteObject.getInt("sectionTime")
                                                )
                                            )
                                            tempMinWalkTime += currentRouteObject.getInt("sectionTime")
                                            // 여기까지 해서 현재 도보일 경우 전체 경로처리는 끝.


                                            // 이제 상세 경로 처리.
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
                                            } // 요기까지 해서 처음과 끝 인덱스가 아니면서 현재 교통 수단이 도보일 경우에 대한 처리 끝.


                                        } else if (currentRouteObject.getInt("trafficType") == 2) {
                                            // 현재가 버스일 경우
                                            // 무조건 layout이 3번임 예외 없음
                                            // 전체 경로에서 필요한 것은 total time table, routes info도 필요함.
                                            layoutType = 3
                                            sectionTime = currentRouteObject.getInt("sectionTime")
                                            startName = currentRouteObject.getString("startName")
                                            var wholeRouteEndName = currentRouteObject.getString("endName")

                                            val currentRouteObjectLane =
                                                currentRouteObject.getJSONArray("lane")
                                            val currentRouteObjectLaneFirstInfo =
                                                currentRouteObjectLane.getJSONObject(0)
                                            busNoORname =
                                                currentRouteObjectLaneFirstInfo.getString("busNo")
                                            Log.d("각 케이스에서의 역 개수가 알고 싶어용",currentRouteObject.getInt("stationCount").toString() )

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
                                            for (k in 0 until currentRouteObjectPassStopListStations.length()) {
                                                // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자.
                                                val currentRouteObjectPassStopListStationsObj =
                                                    currentRouteObjectPassStopListStations.getJSONObject(k)
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

                                                var wholeRouteEndName = currentRouteObject.getString("endName")
                                                startName = currentRouteObject.getString("startName")

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
                                                for (k in 0 until currentRouteObjectPassStopListStations.length()) {
                                                    // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자. 하지만 유의할 건 지하철이므로 미정차 여부는 N으로 채움,
                                                    val currentRouteObjectPassStopListStationsObj =
                                                        currentRouteObjectPassStopListStations.getJSONObject(k)
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

                                                var wholeRouteEndName = currentRouteObject.getString("endName")
                                                startName = currentRouteObject.getString("startName")

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
                                                    currentRouteObjectPassStopList.getJSONArray("stations")
                                                for (k in 0 until currentRouteObjectPassStopListStations.length()) {
                                                    // 일단 station list에 다 넣자. 출발지 목적지 다 포함해서 싹 다 넣자. 하지만 유의할 건 지하철이므로 미정차 여부는 N으로 채움,
                                                    val currentRouteObjectPassStopListStationsObj =
                                                        currentRouteObjectPassStopListStations.getJSONObject(k)
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
                                            resultPathObjSubPath.getJSONObject(j - 1)
                                        var currentRouteObject =
                                            resultPathObjSubPath.getJSONObject(j)

                                        totalTimeTable.add(
                                            Pair(
                                                3, currentRouteObject.getInt("sectionTime")
                                            )
                                        )

                                        tempMinWalkTime += currentRouteObject.getInt("sectionTime")

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

                                    routeItemComponentElement.add(
                                        RouteItemComponent(
                                            i,
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
                                } // sub path 처리 for문

                                // 여기까지 하면 result의 path array에서의 i index에 있는 전체 경로 요약 및 i index의 세부 경로까지 표현이 완료된다.
                                routeItemComponentElement.add(
                                    RouteItemComponent(
                                        i,
                                        9, trafficType, distance, sectionTime, stationCount, busNoORname, startName,endName,
                                        passStopList, door, startExitnoORendExitno, way
                                    )
                                )

                                //요 위 add까지 했을 때 path array i index에 있는 상세 경로에 대한 처리는 완료 되었다.
                                every_person_detail_route_array.add(routeItemComponentElement)
                                whole_route_info.add(
                                    ItemComponent(
                                        i,
                                        pathType,
                                        totalTime,
                                        tempMinWalkTime,
                                        totalFee,
                                        routesInfo,
                                        totalTimeTable
                                    )
                                )

                                loop_init++
                            } // result의 모든 path 처리 for문.

                            Log.d("show result 에서의 최소 인덱스는?", min_index.toString())
                            resultAdapter.addResult(ResultObject(names[loop_i], min_time))
                            loop_i++
                            resultAdapter.addDetailList(every_person_detail_route_array)
                            resultAdapter.addMin(min_index)
                            resultAdapter.addWholeRouteList(whole_route_info)
                            Log.d("데이터가 다 들어갔을까?", "ㅇㅇ")
                            // 여기까지 해서 하나의 경로에 대한 세부사항 처리를 다 했겠지. 이 때 전체 경로 요약 정보를 다 주는건 어떨까
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }

                override fun onError(i: Int, errorMessage: String, api: API) {
                    Log.d("API 호출 실패","실패 했습니다.")
                }
            }

        resultAdapter = ResultAdapter(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())

        recyclerViewResult.adapter = resultAdapter
        recyclerViewResult.layoutManager = LinearLayoutManager(this)



        for(i in 0 until addressObjects.size)names.add(addressObjects[i].user_name)

        for (i in 0 until addressObjects.size) {
            Log.d("latlatlat", addressObjects[i].lat.toString())
            odsayService!!.requestSearchPubTransPath(
                addressObjects[i].lon.toString(),
                addressObjects[i].lat.toString(),
                addressObject.lon.toString(),
                addressObject.lat.toString(),
                0.toString(),
                0.toString(),
                0.toString(),
                onResultCallbackListener
            )
        }


        textView2.text = destinationName + " 까지"

        //저장하기 버튼 클릭시 -> 캘린더, 약속이름 정하러가기
        buttonSave.setOnClickListener {
            var intent = Intent(this, SaveBookmarkActivity::class.java)
            intent.putExtra("startAddressList", addressObjects)
            intent.putExtra("destinationAddress", addressObject)
            startActivity(intent)
        }

        //공유하기 버튼 클릭시
        buttonShare.setOnClickListener {

            val shareAddress = ArrayList<ShareAddress>()
            for (ao in addressObjects) {
                val placeName = ao.place_name
                val username = ao.user_name
                val roadAddressName = ao.road_address_name
                val addressName = ao.address_name
                val lat = ao.lat
                val lon = ao.lon
                shareAddress.add(
                    ShareAddress(
                        placeName,
                        username,
                        roadAddressName,
                        addressName,
                        lat,
                        lon
                    )
                )
            }

            var myShare = Share(
                addressObject.place_name, App.prefs.username.toString(),
                addressObject.road_address_name, addressObject.address_name,
                addressObject.lat, addressObject.lon, shareAddress
            )

            //공유 저장하기 API 호출
            RetrofitManager.instance.saveShareService(
                share = myShare,
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY->{
                            Log.d(TAG, "API 호출 성공 : $responseBody")

                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if (statusCode == 201) {
                                val data = jsonObject.getJSONObject("data")
                                val random_code = data.getString("code")

                                val clipboard: ClipboardManager =
                                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("RANDOM_CODE", random_code)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(this@ShowResultActivity, "공유 코드가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT)
                                    .show()

                            } else {
                                val errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = ${errorMessage}")
                                Toast.makeText(this@ShowResultActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }

                        //API 호출 실패시
                        RESPONSE_STATE.FAIL->{
                            Log.d(TAG, "API 호출 실패 : $responseBody")
                        }
                    }
                }
            )
        }

        //홈버튼 클릭시
        buttonHome.setOnClickListener {
            val intentHome = Intent(applicationContext, MainNewActivity::class.java)
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intentHome)
            finish()
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