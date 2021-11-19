package com.example.meethere.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.ResultAdapter
import com.example.meethere.databinding.ActivityShowBookmarkBinding
import com.example.meethere.databinding.ActivityShowBookmarkResultBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.ResultObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.retrofit.request.Share
import com.example.meethere.retrofit.request.ShareAddress
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_show_bookmark.*
import kotlinx.android.synthetic.main.activity_show_result.*
import kotlinx.android.synthetic.main.activity_show_result.buttonHome
import kotlinx.android.synthetic.main.activity_show_result.buttonShare
import kotlinx.android.synthetic.main.activity_show_result.recyclerViewResult
import kotlinx.android.synthetic.main.activity_show_result.textView2
import org.json.JSONObject

class ShowBookmarkResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowBookmarkResultBinding
    private lateinit var resultAdapter: ResultAdapter
    private var addressObjects = arrayListOf<AddressObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBookmarkResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultAdapter = ResultAdapter(mutableListOf(), mutableListOf())

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
                            Toast.makeText(this@ShowBookmarkResultActivity, message, Toast.LENGTH_SHORT)
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

        binding.buttonEdit.setOnClickListener {
            val intent = Intent(this@ShowBookmarkResultActivity, EditBookmarkActivity::class.java)
            intent.putExtra("bookmarkId",bookmarkId.toString())
            intent.putExtra("promise_name",promise_name)
            intent.putExtra("promise_date",promise_date)
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
                        RESPONSE_STATE.OKAY -> {
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
                                Toast.makeText(
                                    this@ShowBookmarkResultActivity,
                                    "공유 코드가 클립보드에 복사되었습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } else {
                                val errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = ${errorMessage}")
                                Toast.makeText(
                                    this@ShowBookmarkResultActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
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