package com.example.meethere.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.meethere.R
import com.example.meethere.databinding.ActivityShowBookmarkBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject
import kotlin.math.log

class ShowBookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowBookmarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getLongExtra가 안되서 String으로 받아와서 Long으로 변환
        var bookmarkId = intent.getStringExtra("bookmarkId")?.toLong()

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
        )

    }
}