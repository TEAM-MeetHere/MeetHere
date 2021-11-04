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
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.retrofit.request.Share
import com.example.meethere.retrofit.request.ShareAddress
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject


class ShowResultActivity : AppCompatActivity() {
    private lateinit var resultAdapter: ResultAdapter

    private fun startToDetailActivity(id: Int, message: String) {
        val intent = Intent(applicationContext, ShowDetailActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result)

        resultAdapter = ResultAdapter(mutableListOf())

        recyclerViewResult.adapter = resultAdapter
        recyclerViewResult.layoutManager = LinearLayoutManager(this)

        //출발 주소 리스트
        val addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("addressData") as Array<AddressObject>

        //도착 주소
        val addressObject: AddressObject =
            intent.getSerializableExtra("addressObject") as AddressObject
        val destinationName: String? = addressObject.place_name

        for (i in addressObjects.indices) {
            val resultObject = ResultObject(addressObjects[i].user_name, 10) // 예상시간을 적을 예정
            resultAdapter.addResult(resultObject)
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