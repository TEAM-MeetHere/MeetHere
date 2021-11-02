package com.example.meethere.activity

import KakaoAPI
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.AddressObject
import com.example.meethere.ResultSearchKeyword
import com.example.meethere.SearchResultItem
import com.example.meethere.adapter.SearchResultAdapter
import com.example.meethere.databinding.ActivitySelectDestinationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint


class SelectDestinationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectDestinationBinding
    private val listItems = arrayListOf<SearchResultItem>()   // 리사이클러 뷰 아이템
    private val searchResultAdapter = SearchResultAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호

    var averageLat: Double = 0.0
    var averageLon: Double = 0.0

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK d921c75efdd92ffb9c3584afeacee0b6"  // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDestinationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerViewSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSearch.adapter = searchResultAdapter

        // SetLocation에서 넘겨준 입력받은 모든 주소 데이터를 가져옴
        val addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("addressData") as Array<AddressObject>
        val lats = DoubleArray(addressObjects.size)
        val lons = DoubleArray(addressObjects.size)

        // 주소의 평균 지점을 구함
        for (i in addressObjects.indices) {
            lats[i] = addressObjects[i].lat
            lons[i] = addressObjects[i].lon
        }
        averageLat = getMean(lats)
        averageLon = getMean(lons)

        // 지도의 중심점을 평균 지점으로 설정, 확대 레벨 설정 (값이 작을수록 더 확대됨)
        val mapPoint = MapPoint.mapPointWithGeoCoord(averageLat, averageLon)
        binding.mapDestination.setMapCenterPoint(mapPoint, true)
        binding.mapDestination.setZoomLevel(2, true)

        // 마커 생성
        val marker = MapPOIItem()
        marker.itemName = "평균지점"
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.YellowPin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        binding.mapDestination.addPOIItem(marker)
        //


        // 중심점을 기준으로 Integer(5000) (임시값)의 키워드를 검색
        val keyword = intent.getStringExtra("keywordData").toString()
        searchKeyword(keyword, pageNumber, averageLon.toString(), averageLat.toString(), Integer(5000))

        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber, averageLon.toString(), averageLat.toString(), Integer(5000))
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber, averageLon.toString(), averageLat.toString(), Integer(5000))
        }

        searchResultAdapter.setItemClickListener(object : SearchResultAdapter.OnItemClickListener {
            override fun onClick(addressObject: AddressObject, position:Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapDestination.setMapCenterPointAndZoomLevel(mapPoint, 2, true)
            }
        })

        // 최종 목적지를 터치하면 해당 데이터와 입력받은 주소 데이터들을 ShowResult로 넘겨줌
        searchResultAdapter.setItemClickListener2(object : SearchResultAdapter.OnItemClickListener {
            override fun onClick(addressObject: AddressObject, position:Int) {
                val intent = Intent(applicationContext, ShowResult_2_7Activity::class.java)
                intent.putExtra("addressData", addressObjects)
                intent.putExtra("addressObject", addressObject)
                startActivity(intent)
            }
        })
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page:Int, x: String, y: String, radius: Integer) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(
            API_KEY,
            keyword,
            page,
            x,
            y,
            radius
        )   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }

    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapDestination.removeAllPOIItems()
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = SearchResultItem(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(
                        document.y.toDouble(),
                        document.x.toDouble()
                    )
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapDestination.addPOIItem(point)
            }

            binding.mapDestination.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    searchResult.documents[0].y.toDouble(),
                    searchResult.documents[0].x.toDouble()
                ), true
            )

            searchResultAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화
        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSum(numbers: DoubleArray): Double {
        var sum: Double = 0.0

        for (x in numbers) {
            sum += x
        }

        return sum
    }

    private fun getMean(numbers: DoubleArray): Double {
        var sum = getSum(numbers)
        return sum / numbers.size.toDouble()
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