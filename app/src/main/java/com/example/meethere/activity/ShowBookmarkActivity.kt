package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.R
import com.example.meethere.adapter.ResultAdapter
import com.example.meethere.databinding.ActivityShowBookmarkBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.ResultObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_show_result.*
import org.json.JSONObject
import kotlin.math.log

class ShowBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowBookmarkBinding
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultAdapter = ResultAdapter(mutableListOf())

        recyclerViewResult.adapter = resultAdapter
        recyclerViewResult.layoutManager = LinearLayoutManager(this)

        val addressObject: AddressObject =
            intent.getSerializableExtra("addressObject") as AddressObject
        val destinationName: String? = addressObject.place_name

        textView2.text = destinationName + " 까지"

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

                                val resultObject =
                                    ResultObject(username, 10) // 예상시간을 적을 예정
                                resultAdapter.addResult(resultObject)

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

        //공유하기 버튼 클릭시
        buttonShare.setOnClickListener {
            // 아직 미구현
        }

        //홈버튼 클릭시
        buttonHome.setOnClickListener {
            val intentHome = Intent(applicationContext, MainActivity::class.java)
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