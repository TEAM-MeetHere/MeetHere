package com.example.meethere.activity

import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.meethere.R
import com.example.meethere.databinding.ActivityAlgorithm2Binding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.UserStation
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Algorithm2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAlgorithm2Binding

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
        Log.d("수신", id_list.toString())
        var station_length = id_list?.size

        //json 파일 불러오기
        val assetManager: AssetManager = this.resources.assets
        val inputStream = assetManager.open("graphData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jObject = JSONObject(jsonString)

        //최대 시간
        var max_time = 10

        //최종 중심역 리스트가 비어있다면
        while (answer.isEmpty()) {
            Log.d("현재 최대 시간 ", max_time.toString())
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

                    Log.d("출발역 ", station_name)
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
                    Log.d("userStation$i = ", userStation.toString())
                    userStation_list.add(userStation)
                }
                endId_list.sort()

                Log.d("통합 목적지 리스트(중복X)", endId_set.toString())
                Log.d("통합 목적지 리스트(중복O)", endId_list.toString())

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
            Log.d("max time ", max_time.toString())
            Log.d("최종 중심역 리스트", answer.toString())


            var transfer_list = ArrayList<String>()
            var transfer_name_list = ArrayList<String>()
            var result_list = ArrayList<String>()

            for (i in id_list.indices) {
                val target = id_list[i]
                val station_name = jObject.getJSONObject(target).getString("station_name")

                Log.d("출발 역 이름 ", station_name)
            }

            for (i in 0 until answer.size) {
                val target = answer[i]
                val station_name = jObject.getJSONObject(target).getString("station_name")
                val station_transfer = jObject.getJSONObject(target).getBoolean("station_transfer")
                if (station_transfer && !transfer_name_list.contains(station_name)) {
                    transfer_list.add(target)
                    transfer_name_list.add(station_name)
                }
                Log.d("최종 역 이름 ", station_name)
            }

            //환승역이 존재하지 않으면 기존 최종 역 리스트 사용
            if (transfer_list.isEmpty()) {
                result_list = answer
            }
            //환승역이 존재하면 환승역 리스트 사용
            else {
                result_list = transfer_list
            }

            Log.d("result_list ", result_list.toString())
            Log.d("userStation_list.size ", userStation_list.size.toString())
            Log.d("userStation_list ", userStation_list.toString())


            var lat: Double = 0.0
            var lon: Double = 0.0

            for (i in 0 until result_list.size) {
                val ja = jObject.getJSONObject(result_list[i]) //출발역에 대한 JSONObject

                val station_lat = ja.getDouble("station_lat") //출발역 이름
                val station_lon = ja.getDouble("station_lon") //환승역 존재 여부

                lat += station_lat
                lon += station_lon
            }

            lat /= result_list.size
            lon /= result_list.size

            Log.d("최종 Lat ", lat.toString())
            Log.d("최종 Lon ", lon.toString())

            val intent = Intent(this@Algorithm2Activity, SelectDestinationActivity::class.java)
            intent.putExtra("keywordData", keywordData)
            intent.putExtra("addressData", addressObjects)
            intent.putExtra("isTrue", isTrue)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)
            startActivity(intent)
            finish()
        }
    }
}