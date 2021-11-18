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
                            var one_person_detail_route_array: MutableList<RouteItemComponent> =
                                arrayListOf()
                            var result = ODsayData.json.getJSONObject("result")
                            var min_time: Int = 999999999
                            var min_index: Int = 0
                            Log.d("resultasdf", result.toString())
                            var resultBest = result.getJSONArray("path")
                            for (i in 0 until resultBest.length()) {
                                var resultBestOBJ = resultBest.getJSONObject(i)
                                var resultBestOBJINFO = resultBestOBJ.getJSONObject("info")
                                if (resultBestOBJINFO.getInt("totalTime") < min_time) {
                                    min_time = resultBestOBJINFO.getInt("totalTime")
                                    min_index = i
                                }
                            }
                            Log.d("resultBest", resultBest.toString())
                            var resultBestOBJ = resultBest.getJSONObject(0)
                            var resultBestOBJINFO = resultBestOBJ.getJSONObject("info")
                            resultAdapter.addResult(ResultObject(names[loop_i], min_time))
                            loop_i++
                            Log.d("loop_i", loop_i.toString())
                            Log.d("최소 시간", min_time.toString())

                            // 여기서 시간 판별,최소 시간인덱스가 min_index. (일단은 얘에 대한 상세 경로를 상세보기 눌렀을 때 보여줘야 하니깐)
                            var minResultPathObject = resultBest.getJSONObject(min_index)
                            var minResultPathObjectInfo = minResultPathObject.getJSONObject("info")
                            var minResultPathObjectSubPath =
                                minResultPathObject.getJSONArray("subPath")
                            resultAdapter.addTimeWalkFee(
                                TimeWalkFee(
                                minResultPathObjectInfo.getInt("totalWalk"),
                                minResultPathObjectInfo.getInt("totalTime"),
                                minResultPathObjectInfo.getInt("payment")
                                )
                            )
                            var routeItemList: MutableList<RouteItemComponent> = arrayListOf()
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
                            one_person_detail_route_array.add(
                                RouteItemComponent(9, trafficType, distance, sectionTime, stationCount, busNoORname, startName,endName,
                                passStopList, door, startExitnoORendExitno, way)
                            )
                            resultAdapter.addDetailList(one_person_detail_route_array)
                            Log.d("데이터가 다 들어갔을까?", "ㅇㅇ")
                        }


//                            var one_person_route_array : MutableList<ItemComponent> = arrayListOf()
//                            var resultPathArray = result.getJSONArray("path")
//                            for (i in 0 until resultPathArray.length()) {
//                                var resultPathArrayObj = resultPathArray.getJSONObject(i)
//                                var resultPathArrayInfoObj =
//                                    resultPathArrayObj.getJSONObject("info")
//                                var pathType = resultPathArrayObj.getInt("pathType")
//                                var totalTime = resultPathArrayInfoObj.getInt("totalTime")
//                                var totalWalk = resultPathArrayInfoObj.getInt("totalWalk")
//                                var payment = resultPathArrayInfoObj.getInt("payment")
//                                var routesInfo: MutableList<String> = arrayListOf()
//                                var totalTimeTable: MutableList<Pair<Int, Int>> = arrayListOf()
//                                var resultPathArrayObjSubPath =
//                                    resultPathArrayObj.getJSONArray("subPath")
//                                for (j in 0 until resultPathArrayObjSubPath.length()) {
//                                    var resultPathArrayObjSubPathObj =
//                                        resultPathArrayObjSubPath.getJSONObject(j)
//                                    if (resultPathArrayObjSubPathObj.getInt("trafficType") == 1) {
//                                        // 지하철
//                                        var transportLaneInfo =
//                                            resultPathArrayObjSubPathObj.getJSONArray("lane")
//                                        totalTimeTable.add(
//                                            Pair(
//                                                3,
//                                                resultPathArrayObjSubPathObj.getInt("sectionTime")
//                                            )
//                                        )
//                                        var transportLaneInfoObj =
//                                            transportLaneInfo.getJSONObject(0)
//                                        routesInfo.add(transportLaneInfoObj.getString("name"))
//                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("startName"))
//                                        routesInfo.add("탑승")
//                                        routesInfo.add(transportLaneInfoObj.getString("name"))
//                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("endName"))
//                                        routesInfo.add("하차")
//
//                                    } else if (resultPathArrayObjSubPathObj.getInt("trafficType") == 2) {
//                                        // 버스
//                                        var transportLaneInfo =
//                                            resultPathArrayObjSubPathObj.getJSONArray("lane")
//                                        totalTimeTable.add(
//                                            Pair(
//                                                2,
//                                                resultPathArrayObjSubPathObj.getInt("sectionTime")
//                                            )
//                                        )
//                                        var transportLaneInfoObj =
//                                            transportLaneInfo.getJSONObject(0)
//                                        routesInfo.add(transportLaneInfoObj.getString("busNo"))
//                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("startName"))
//                                        routesInfo.add("탑승")
//                                        routesInfo.add(transportLaneInfoObj.getString("busNo"))
//                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("endName"))
//                                        routesInfo.add("하차")
//                                    } else {
//                                        // 도보
//                                        if (resultPathArrayObjSubPathObj.getInt("sectionTime") != 0) {
//                                            totalTimeTable.add(
//                                                Pair(
//                                                    1,
//                                                    resultPathArrayObjSubPathObj.getInt("sectionTime")
//                                                )
//                                            )
//                                        }
//                                    }
//                                }
//                                //여기까지 하면 하나의 result에 대한 정보를 다 받아옴.
//                                one_person_route_array.add(
//                                    ItemComponent(
//                                        pathType,
//                                        totalTime,
//                                        totalWalk,
//                                        payment,
//                                        routesInfo,
//                                        totalTimeTable
//                                    )
//                                )
//                            }
//                            resultAdapter.addRouteList(one_person_route_array)
//                            Log.d("데이터가 다 들어갔을까?", "ㅇㅇ")
//                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }

                override fun onError(i: Int, errorMessage: String, api: API) {
                    Log.d("API 호출 실패","실패 했습니다.")
                }
            }

        resultAdapter = ResultAdapter(mutableListOf(), mutableListOf())

        recyclerViewResult.adapter = resultAdapter
        recyclerViewResult.layoutManager = LinearLayoutManager(this)

        //출발 주소 리스트
        val addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("addressData") as Array<AddressObject>

        //도착 주소
        val addressObject: AddressObject =
            intent.getSerializableExtra("addressObject") as AddressObject
        val destinationName: String? = addressObject.place_name


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