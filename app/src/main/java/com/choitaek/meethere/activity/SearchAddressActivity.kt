package com.choitaek.meethere.activity

import KakaoAPI
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.recyclerview.widget.LinearLayoutManager
import com.choitaek.meethere.objects.AddressObject
import com.choitaek.meethere.objects.ResultSearchKeyword
import com.choitaek.meethere.objects.SearchResultObject
import com.choitaek.meethere.adapter.SearchResultAdapter
import com.choitaek.meethere.databinding.ActivitySearchAddressBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 주소를 검색하는 액티비티
class SearchAddressActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK d921c75efdd92ffb9c3584afeacee0b6"  // REST API 키
    }

    private lateinit var binding: ActivitySearchAddressBinding
    private val listItems = arrayListOf<SearchResultObject>()   // 리사이클러 뷰 아이템
    private val listAdapter = SearchResultAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchAddressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter

        listAdapter.setItemClickListener(object : SearchResultAdapter.OnItemClickListener {
            override fun onClick(addressObject: AddressObject, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].lat, listItems[position].lon)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 2, true)
            }
        })

        listAdapter.setItemClickListener2(object : SearchResultAdapter.OnItemClickListener {
            override fun onClick(addressObject: AddressObject, position: Int) {
                if (addressObject.road_address_name == "") {
                    Toast.makeText(
                        this@SearchAddressActivity,
                        "도로명 주소가 있는 장소를 선택해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("임영택 SearchAddress - lat : ",addressObject.lat.toString())
                Log.d("임영택 SearchAddress - lon : ",addressObject.lon.toString())

                val intent = intent
                intent.putExtra("addressObject", addressObject)
                // 오브젝트를 이전 액티비티로 전달해줌
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        // 검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 초기 기본 위치로 카메라 이동
        val mapPoint = MapPoint.mapPointWithGeoCoord(37.4499641433847, 126.653467210032)
        binding.mapView.setMapCenterPoint(mapPoint, true)
        binding.mapView.setZoomLevel(2, true)
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchAddress(API_KEY, keyword, page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = SearchResultObject(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.y.toDouble(),
                    document.x.toDouble()
                )
                listItems.add(item)

                // 지도에 마커 추가
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
                binding.mapView.addPOIItem(point)
            }

            binding.mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    searchResult.documents[0].y.toDouble(),
                    searchResult.documents[0].x.toDouble()
                ), true
            )

            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}