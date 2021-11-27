package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.databinding.ActivityShowBookmarkBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.BookmarkObject

class ShowBookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowBookmarkBinding

    lateinit var bookmarkObjects: MutableList<BookmarkObject>
    lateinit var bookmarkAdapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShowBookmarkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        resultAdapter = ResultAdapter(mutableListOf(), mutableListOf(), mutableListOf(),
            mutableListOf(),mutableListOf(),mutableListOf())

        recyclerViewResult.adapter = resultAdapter
        recyclerViewResult.layoutManager = LinearLayoutManager(this)

        val addressObject: AddressObject =
            intent.getSerializableExtra("addressObject") as AddressObject
        val destinationName: String? = addressObject.place_name

        textView2.text = destinationName + " 까지"

        //getLongExtra가 안되서 String으로 받아와서 Long으로 변환
        var bookmarkId = intent.getStringExtra("bookmarkId")?.toLong()
        val promise_date : String = intent.getStringExtra("promise_date")!!
        val promise_name : String = intent.getStringExtra("promise_name")!!

        RetrofitManager.instance.findStartAddressListService(
            bookmarkId = bookmarkId!!,
            completion = { responseState, responseBody ->
                when (responseState) {

                    //API 호출 성공시
                    RESPONSE_STATE.OKAY -> {
                        Log.d(TAG, "API 호출 성공 : $responseBody")

                        val jsonObject = JSONObject(responseBody)
                        val statusCode = jsonObject.getInt("statusCode")

                        if (statusCode == 200) {
                            val message = jsonObject.getString("message")
                            val dataArray = jsonObject.getJSONArray("data")

                            for (i in 0..dataArray.length() - 1) {
                                val iObject = dataArray.getJSONObject(i)
                                val placeName = iObject.getString("placeName")
                                val username = iObject.getString("username")
                                val roadAddressName = iObject.getString("roadAddressName")
                                val addressName = iObject.getString("addressName")
                                val lat = iObject.getDouble("lat")
                                val lon = iObject.getDouble("lon")

                                val resultObject =
                                    ResultObject(username, 10) // 예상시간을 적을 예정
                                resultAdapter.addResult(resultObject)

                                addressObjects.add(
                                    AddressObject(
                                        placeName,
                                        username,
                                        roadAddressName,
                                        addressName,
                                        lat,
                                        lon
                                    )
                                )

                                Log.d(TAG, "$i 번째 출발 주소")
                                Log.d(TAG, "placeName = $placeName")
                                Log.d(TAG, "username = $username")
                                Log.d(TAG, "roadAddressName = $roadAddressName")
                                Log.d(TAG, "addressName = $addressName")
                                Log.d(TAG, "lat = $lat")
                                Log.d(TAG, "lon = $lon")
                            }
                        } else {
                            val message = jsonObject.getString("message")
                            Log.d(TAG, "response message = $message")
                            Toast.makeText(this@ShowBookmarkActivity, message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    //API 호출 실패시
                    RESPONSE_STATE.FAIL -> {
                        Log.d(TAG, "API 호출 실패 : $responseBody")
                    }
                }
            }
        }
        else binding.tvShowBookmark.text = "전체 일정"

        bookmarkAdapter.notifyDataSetChanged()

        bookmarkAdapter.setItemClickListener(object : BookmarkAdapter.OnItemClickListener {
            override fun onClick(
                promise_id: Long,
                addressObject: AddressObject,
                promise_name: String,
                promise_date: String,
                position: Int,
            ) {
                val intent =
                    Intent(this@ShowBookmarkActivity, ShowBookmarkResultActivity::class.java)
                intent.putExtra("bookmarkId", promise_id.toString())
                intent.putExtra("addressObject", addressObject)
                intent.putExtra("promise_name", promise_name)
                intent.putExtra("promise_date", promise_date)
                startActivity(intent)
            }
        })
    }
}