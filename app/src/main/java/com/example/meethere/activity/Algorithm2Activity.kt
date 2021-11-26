package com.example.meethere.activity

import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.meethere.R
import com.example.meethere.databinding.ActivityAlgorithm2Binding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.ResultObject
import com.example.meethere.objects.UserStation
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap as mutableMap

class Algorithm2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAlgorithm2Binding

    private var odsayService: ODsayService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlgorithm2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("In Algorithm2 ", "테스트 시작")

        // Set Location에서 넘겨준 입력받은 모든 주소 데이터를 가져옴
        val addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("addressData") as Array<AddressObject>

        val keywordData = intent.getStringExtra("keywordData")
        val isTrue = intent.getBooleanExtra("isTrue", false)

        //최종 중심역 리스트
        var answer = ArrayList<String>()

        //출발지점 리스트
        var id_list = intent.getStringArrayListExtra("startStationList")

        var station_length = id_list?.size

        var nextActivityFlag = 0

        //json 파일 불러오기
        val assetManager: AssetManager = this.resources.assets
        val inputStream = assetManager.open("graphData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jObject = JSONObject(jsonString)

        //최대 시간
        var max_time = 10

        //최종 중심역 리스트가 비어있다면
        while (answer.isEmpty()) {
            Log.d("테스트 : 현재 최대 시간 ", max_time.toString())
            val endId_list = ArrayList<String>() //도착역 ID 저장 리스트(중복 O)
            val endId_set = ArrayList<String>() //도착역 ID 저장 리스트(중복 X)
            var userStation_list = ArrayList<UserStation>()

            //i 번째 출발지점에서 max_time 까지의 역 리스트 저장
            for (i in id_list!!.indices) {

                var start_station_queue: Queue<String> = LinkedList()
                val start_station = id_list[i]
                start_station_queue.offer(start_station)

//                start_list.add(start_station_queue)

                var next_s = mutableMapOf<String, Int>()
                next_s[start_station] = 0
                var userStation = UserStation(next_s)

                //max_time 도달하기 전까지 반복(출발역이 없을 때까지 반복)
                while (!start_station_queue.isEmpty()) {

                    val start = start_station_queue.poll()!! //새로운 출발역

                    val jArray = jObject.getJSONObject(start) //출발역에 대한 JSONObject

                    val station_name = jArray.getString("station_name") //출발역 이름
                    val station_transfer = jArray.getBoolean("station_transfer") //환승역 존재 여부

                    val station_railroad = jArray.getJSONArray("station_railroad")

                    //다음역(prev, next)에 대한 for 문 (prev, next) -> next_station 통합함
                    for (i in 0 until station_railroad.length()) {

                        val jjArray = station_railroad.getJSONObject(i)
                        val railroad_time = jjArray.getString("railroad_time")
                        val railroad_line = jjArray.getString("railroad_line")
                        val railroad_endID = jjArray.getString("railroad_endID")

                        //다음역 ID가 기존 key 에 존재하지 않으면
                        if (!userStation.next_station.containsKey(railroad_endID)) {
                            //다음역 ID 까지 소요시간 갱신
                            userStation.next_station[railroad_endID] =
                                userStation.next_station[start]!! +
                                        railroad_time.toInt()

                            //환승역이 존재하면 소요시간 +1
                            if (station_transfer) {
                                userStation.next_station[railroad_endID] =
                                    userStation.next_station[railroad_endID]!! + 1
                            }

                            //max_time 내의 역들을 저장
                            endId_list.add(railroad_endID)

                            //겹치지 않는 리스트
                            if (!endId_set.contains(railroad_endID)) {
                                endId_set.add(railroad_endID)
                            }

                            //max_time 을 초과하지 않았다면, 새로운 출발역 리스트에 저장
                            if (userStation.next_station[railroad_endID]!! < max_time) {
                                start_station_queue.offer(railroad_endID)
                            } else {
                                userStation.next_station.remove(railroad_endID)
                                endId_list.remove(railroad_endID)

                                if (endId_set.contains(railroad_endID)) {
                                    endId_set.remove(railroad_endID)
                                }
                            }
                        }
                    }

                    userStation_list.add(userStation)
                }
                endId_list.sort()

                Log.d("테스트 : 통합 목적지 리스트(중복X)", endId_set.toString())
                Log.d("테스트 : 통합 목적지 리스트(중복O)", endId_list.toString())

                for (i in 0 until endId_set.size) {
                    val target = endId_set[i]

                    if (endId_list.count { it == target } == station_length) {
                        Log.d("$station_length 개 존재", target)

                        if (!answer.contains(target)) {
                            answer.add(target)
                        }
                    }
                }
            }

            if (answer.isEmpty()) {
                max_time += 10
            }

            Log.d("테스트 : max time ", max_time.toString())
            Log.d("테스트 : 최종 중심역 리스트", answer.toString())

            var transfer_list = ArrayList<String>()
            var transfer_name_list = ArrayList<String>()
            var result_list = ArrayList<String>()

            for (i in id_list.indices) {
                val target = id_list[i]
            }

            for (i in 0 until answer.size) {
                val target = answer[i]
                val station_name = jObject.getJSONObject(target).getString("station_name")
                val station_transfer = jObject.getJSONObject(target).getBoolean("station_transfer")
                if (station_transfer && !transfer_name_list.contains(station_name)) {
                    transfer_list.add(target)
                    transfer_name_list.add(station_name)
                }
                Log.d("테스트 : 최종 역 이름 ", station_name)
            }
/*

            //디버그 코드
            answer.add("102")
            answer.add("103")
            // 삭제바람
*/

            if (answer.size != 0) {
                odsayService =
                    ODsayService.init(this, "hQVkqz/l8aEPCdgn6JlDWk793L3D/rl5Cyko3JqKhcw")

                // 시간을 몽땅 긁어올 리스트
                var TimeList = mutableListOf<Int>()


                val answersize = answer.size
                val addressObjectsSize = addressObjects.size

                var onResultCallbackListener: OnResultCallbackListener =
                    object : OnResultCallbackListener {
                        // 호출성공시 onSuccess -> 이 안에서 뭘 하냐
                        // 호출을 성공했을 때 fragment에 data를 뿌려주고 싶은데....................
                        // API호출 결과 데이터 리턴.
                        override fun onSuccess(ODsayData: ODsayData, api: API) {
                            nextActivityFlag += 1
                            Log.d("테스트 : API호출 성공", "성공")

                            var min_time: Int = 999999999
                            var result = ODsayData.json.getJSONObject("result")
                            Log.d("테스트 : jsonObject : ", "$result")
                            var resultBest = result.getJSONArray("path")

                            for (i in 0 until resultBest.length()) {
                                var resultBestOBJ = resultBest.getJSONObject(i)
                                var resultBestOBJINFO = resultBestOBJ.getJSONObject("info")
                                if (resultBestOBJINFO.getInt("totalTime") < min_time)
                                    min_time = resultBestOBJINFO.getInt("totalTime")
                            }

                            TimeList.add(min_time)

                            // 마지막 연산
                            if ((answersize * addressObjectsSize) == nextActivityFlag) {

                                // 각 시간들을 비교할 배열
                                var TimeArray = Array(answersize) { 0 }
                                for (i in 0 until answersize) {
                                    for (j in 0 until addressObjectsSize) {
                                        TimeArray[i] += TimeList[i * addressObjectsSize + j]
                                    }
                                }

                                // 시간들의 리스트를 단순이 배열로 변경한 것 뿐
                                var TimeListAr:Array<Int> = TimeList.toTypedArray()

                                var minPosition = 0
                                for (i in 0 until answersize) {

                                    // 같거나 작으면 편차계산
                                    if (TimeArray[i] <= TimeArray[minPosition]) {
                                        Log.d("테스트 : tempArray1 : ",(minPosition * addressObjectsSize).toString() + " ~ " + (minPosition * addressObjectsSize + addressObjectsSize).toString())
                                        var tempArray =
                                            TimeListAr.copyOfRange(minPosition * addressObjectsSize,
                                                minPosition * addressObjectsSize + addressObjectsSize)
                                        Log.d("테스트 : tempArray2 : ",(i * addressObjectsSize).toString() + " ~ " + (i * addressObjectsSize + addressObjectsSize).toString())
                                        var tempArray2 =
                                            TimeListAr.copyOfRange(i * addressObjectsSize,
                                                i * addressObjectsSize + addressObjectsSize)

                                        var Deviation1 = calculateSD(tempArray)
                                        var Deviation2 = calculateSD(tempArray2)
                                        if (Deviation2 < Deviation1) {
                                            minPosition = i
                                            Log.d("테스트 : i 수치 : ", i.toString())
                                            Log.d("테스트 : Deviation1 : ", Deviation1.toString())
                                            Log.d("테스트 : Deviation2 : ", Deviation2.toString())
                                        }
                                    }
                                }

                                Log.d("테스트 : minPosition : ", minPosition.toString())

                                val finalStationObject = jObject.getJSONObject(answer[minPosition])

                                Log.d("테스트 : finalStationObject : ", finalStationObject.toString())

                                var lat = finalStationObject.getDouble("station_lat")
                                var lon = finalStationObject.getDouble("station_lon")

                                val intent = Intent(this@Algorithm2Activity,
                                    SelectDestinationActivity::class.java)
                                intent.putExtra("keywordData", keywordData)
                                intent.putExtra("addressData", addressObjects)
                                intent.putExtra("isTrue", isTrue)
                                intent.putExtra("lat", lat)
                                intent.putExtra("lon", lon)
                                startActivity(intent)
                                finish()
                            }

                            try {
                                if (api == API.SEARCH_PUB_TRANS_PATH) {
                                    Log.d("TAG", "onSuccess: ")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onError(i: Int, errorMessage: String, api: API) {
                            Log.d("API 호출 실패", "실패 했습니다.")
                        }
                    }


                Log.d("테스트 : ", answer.size.toString() + " / " + addressObjects.size.toString())
                for (i in 0 until answer.size) {
                    val jArray = jObject.getJSONObject(answer[i]) //출발역에 대한 JSONObject
                    val station_lat = jArray.getString("station_lat")
                    val station_lon = jArray.getString("station_lon")

                    for (j in addressObjects.indices) {

                        Log.d("테스트 : ", answer[i] + "역에서" + addressObjects[j].user_name + "까지의 계산")
                        odsayService!!.requestSearchPubTransPath(
                            addressObjects[j].lon.toString(),
                            addressObjects[j].lat.toString(),
                            station_lon,
                            station_lat,
                            0.toString(),
                            0.toString(),
                            0.toString(),
                            onResultCallbackListener
                        )
                    }
                }
            }
        }
    }

    fun calculateSD(numArray: Array<Int>): Double {
        var sum = 0.0
        var standardDeviation = 0.0

        for (num in numArray) {
            sum += num
        }

        val mean = sum / 10

        for (num in numArray) {
            standardDeviation += Math.pow(num - mean, 2.0)
        }

        return Math.sqrt(standardDeviation / 10)
    }
}