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
import com.example.meethere.objects.AddressObject
import com.example.meethere.R
import com.example.meethere.objects.ResultObject
import com.example.meethere.adapter.ResultAdapter
import com.example.meethere.objects.ItemComponent
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
                        if(api == API.SEARCH_PUB_TRANS_PATH){
                            // 이 안에서 할 일. 먼저 전체 경로가 몇 개일까여?
                            // busCount + subwayCount + subwayBusCount를 더한 것이 dynamic button의 개수
                            var result = ODsayData.json.getJSONObject("result")
                            var min_time : Int = 999999999
                            Log.d("resultasdf", result.toString())
                            var resultBest = result.getJSONArray("path")
                            for(i in 0 until resultBest.length()){
                                var resultBestOBJ = resultBest.getJSONObject(i)
                                var resultBestOBJINFO = resultBestOBJ.getJSONObject("info")
                                if(resultBestOBJINFO.getInt("totalTime") < min_time) min_time = resultBestOBJINFO.getInt("totalTime")
                            }
                            Log.d("resultBest", resultBest.toString())
                            var resultBestOBJ = resultBest.getJSONObject(0)
                            var resultBestOBJINFO = resultBestOBJ.getJSONObject("info")
                            resultAdapter.addResult(ResultObject(names[loop_i], min_time))
                            loop_i++


                            var one_person_route_array : MutableList<ItemComponent> = arrayListOf()
                            var resultPathArray = result.getJSONArray("path")
                            for (i in 0 until resultPathArray.length()) {
                                var resultPathArrayObj = resultPathArray.getJSONObject(i)
                                var resultPathArrayInfoObj =
                                    resultPathArrayObj.getJSONObject("info")
                                var pathType = resultPathArrayObj.getInt("pathType")
                                var totalTime = resultPathArrayInfoObj.getInt("totalTime")
                                var totalWalk = resultPathArrayInfoObj.getInt("totalWalk")
                                var payment = resultPathArrayInfoObj.getInt("payment")
                                var routesInfo: MutableList<String> = arrayListOf()
                                var totalTimeTable: MutableList<Pair<Int, Int>> = arrayListOf()
                                var resultPathArrayObjSubPath =
                                    resultPathArrayObj.getJSONArray("subPath")
                                for (j in 0 until resultPathArrayObjSubPath.length()) {
                                    var resultPathArrayObjSubPathObj =
                                        resultPathArrayObjSubPath.getJSONObject(j)
                                    if (resultPathArrayObjSubPathObj.getInt("trafficType") == 1) {
                                        // 지하철
                                        var transportLaneInfo =
                                            resultPathArrayObjSubPathObj.getJSONArray("lane")
                                        totalTimeTable.add(
                                            Pair(
                                                3,
                                                resultPathArrayObjSubPathObj.getInt("sectionTime")
                                            )
                                        )
                                        var transportLaneInfoObj =
                                            transportLaneInfo.getJSONObject(0)
                                        routesInfo.add(transportLaneInfoObj.getString("name"))
                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("startName"))
                                        routesInfo.add("탑승")
                                        routesInfo.add(transportLaneInfoObj.getString("name"))
                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("endName"))
                                        routesInfo.add("하차")

                                    } else if (resultPathArrayObjSubPathObj.getInt("trafficType") == 2) {
                                        // 버스
                                        var transportLaneInfo =
                                            resultPathArrayObjSubPathObj.getJSONArray("lane")
                                        totalTimeTable.add(
                                            Pair(
                                                2,
                                                resultPathArrayObjSubPathObj.getInt("sectionTime")
                                            )
                                        )
                                        var transportLaneInfoObj =
                                            transportLaneInfo.getJSONObject(0)
                                        routesInfo.add(transportLaneInfoObj.getString("busNo"))
                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("startName"))
                                        routesInfo.add("탑승")
                                        routesInfo.add(transportLaneInfoObj.getString("busNo"))
                                        routesInfo.add(resultPathArrayObjSubPathObj.getString("endName"))
                                        routesInfo.add("하차")
                                    } else {
                                        // 도보
                                        if (resultPathArrayObjSubPathObj.getInt("sectionTime") != 0) {
                                            totalTimeTable.add(
                                                Pair(
                                                    1,
                                                    resultPathArrayObjSubPathObj.getInt("sectionTime")
                                                )
                                            )
                                        }
                                    }
                                }
                                //여기까지 하면 하나의 result에 대한 정보를 다 받아옴.
                                one_person_route_array.add(
                                    ItemComponent(
                                        pathType,
                                        totalTime,
                                        totalWalk,
                                        payment,
                                        routesInfo,
                                        totalTimeTable
                                    )
                                )
                            }
                            resultAdapter.addRouteList(one_person_route_array)
                            Log.d("데이터가 다 들어갔을까?", "ㅇㅇ")
                        }
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