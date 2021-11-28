package com.example.meethere.activity

import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.meethere.R
import com.example.meethere.databinding.ActivityAlgorithm1Binding
import com.example.meethere.objects.AddressObject
import com.example.meethere.utils.Constants.TAG
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONObject
import java.lang.Exception

class Algorithm1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAlgorithm1Binding
    private var odsayService: ODsayService? = null
    private lateinit var jsonObject: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlgorithm1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("In Algorithm1 ", "테스트 시작")

        // SetLocation 에서 넘겨준 입력받은 모든 주소 데이터를 가져옴
        val addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("addressData") as Array<AddressObject>

        val keywordData = intent.getStringExtra("keywordData")

        //출발지점 주변역 저장 리스트
        var startStationList = ArrayList<String>()
        var isExistStation: Boolean = true

        //검색한 출발지점 개수
        var idx = 0

        //json 파일 불러오기
        val assetManager: AssetManager = this.resources.assets
        val inputStream = assetManager.open("graphData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jObject = JSONObject(jsonString)

        val onResultCallbackListener: OnResultCallbackListener =
            object : OnResultCallbackListener {
                override fun onSuccess(ODsayData: ODsayData?, p1: API?) {
                    Log.d("API 호출 성공", "성공")
                    jsonObject = ODsayData!!.json
                    idx += 1 //검색 횟수 증가

                    val result = jsonObject.getJSONObject("result")
                    val station = result.getJSONArray("station")

                    //주변역이 존재하지 않아 아무것도 받아오지 못한 경우
                    if (station.length() == 0) {
                        binding.progressBarAlgorithm1.progress = 100
                        isExistStation = false
                        Log.d("In SearchStationActivity", "주변에 역이 존재하지 않습니다.")
                        val intent =
                            Intent(this@Algorithm1Activity, SelectDestinationActivity::class.java)
                        intent.putExtra("addressData", addressObjects)
                        intent.putExtra("isTrue", false)
                        intent.putExtra("keywordData", keywordData)

                        startActivity(intent)
                        finish()
                    }
                    //주변역이 존재하는 경우
                    else {
                        //주변역 데이터 확인
                        for (i in 0 until station.length()) {
                            val iObject = station.getJSONObject(i)
                            val stationId = iObject.getString("stationID")
                            val stationName = iObject.getString("stationName")

                            //json 파일내에 지하철역 ID가 존재하는지 확인
                            try {
                                val find_id = jObject.getJSONObject(stationId)
                                startStationList.add(stationId)
                                Log.d("stationName ", stationName)
                                break
                            }
                            //json 파일내에 지하철역 ID가 존재하지 않는다면(수도권 역이 아니라면)
                            catch (ex: Exception) {
                                isExistStation = false
                                Log.d("In SearchStationActivity", "수도권 밖의 역입니다.")
                                val intent =
                                    Intent(this@Algorithm1Activity, SelectDestinationActivity::class.java)
                                intent.putExtra("addressData", addressObjects)
                                intent.putExtra("keywordData", keywordData)
                                intent.putExtra("isTrue", false)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    //지금까지 저장된 출발지점 주변역 리스트
                    Log.d("startStationList = ", startStationList.toString())

                    //몇번째 계산
                    Log.d("idx ", idx.toString())

                    //모든 출발지점에 대해 검색했다면
                    if (idx == addressObjects.size) {
                        Log.d("마지막", startStationList.toString())

                        //출발지점 주변에 모두 수도권내의 역이 존재하다면
                        if (isExistStation) {
                            val intent =
                                Intent(this@Algorithm1Activity, Algorithm2Activity::class.java)
                            intent.putExtra("startStationList", startStationList)
                            intent.putExtra("addressData", addressObjects)
                            intent.putExtra("keywordData", keywordData)
                            intent.putExtra("isTrue", true)
                            Log.d("전송", startStationList.toString())
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onError(p0: Int, p1: String?, p2: API?) {
                }
            }

        odsayService =
            ODsayService.init(this, getString(R.string.odsay_key)) // api가져옴.

        fun searchStation(x: String, y: String, radius: String) {
            odsayService!!.requestPointSearch(x, y, radius, "2", onResultCallbackListener)
        }

        for (i in addressObjects.indices) {
            if (!isExistStation) {
                Log.d("MESSAGE ", "주변에 존재하는 역이 없는 출발지가 있습니다")
                break
            }
            searchStation(
                addressObjects[i].lon.toString(),
                addressObjects[i].lat.toString(),
                "1500"
            )
        }
    }
}