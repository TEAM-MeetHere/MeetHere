package com.choitaek.meethere.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.choitaek.meethere.objects.AddressObject
import com.choitaek.meethere.databinding.ActivitySaveBookmarkBinding
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.retrofit.request.Bookmark
import com.choitaek.meethere.retrofit.request.StartAddress
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class SaveBookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //캘린더 데이터
        var year: String = ""
        var month: String = ""
        var day: String = ""
        var myDate: String = ""

        //약속 이름
        var dateName: String = ""

        //출발, 도착 주소 리스트 밭아오기
        var addressObjects: Array<AddressObject> =
            intent.getSerializableExtra("startAddressList") as Array<AddressObject>
        var addressObject: AddressObject =
            intent.getSerializableExtra("destinationAddress") as AddressObject

        var myStartAddress = ArrayList<StartAddress>()

        for (addressObject in addressObjects) {

            val placeName = addressObject.place_name
            val userName = addressObject.user_name
            val roadAddressName = addressObject.road_address_name
            val addressName = addressObject.address_name
            val lat = addressObject.lat
            val lon = addressObject.lon

            myStartAddress.add(StartAddress(placeName, userName, roadAddressName, addressName, lat, lon))
        }

        addressObject.user_name = App.prefs.username.toString() //도착지에 username 로그인 유저로 대

        //캘린더 클릭
        binding.etBookmarkDate.setOnClickListener {
            //캘린더
            val cal = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                Toast.makeText(this, "$y-${m + 1}-$d", Toast.LENGTH_SHORT).show()

                year = y.toString()
                month = (m + 1).toString()
                day = d.toString()

                myDate = "$year-$month-$day"
                Log.d(TAG, "약속 날짜 = $myDate")

                binding.etBookmarkDate.setText(myDate)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        //저장 버튼 클릭시
        binding.saveBtn.setOnClickListener {
            dateName = binding.etBookmarkName.text.toString()
            //약속 장소 입력되는 경우에만 진행
            if (dateName != "" && myDate != "") {
                Toast.makeText(this, "즐겨찾기 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                //로그 출력
                Log.d(TAG, "약속 날짜 = $myDate")
                Log.d(TAG, "약속 이름 = $dateName")
                for (addressOb in addressObjects) {
                    Log.d(TAG, "출발지 주소 = $addressOb")
                }
                Log.d(TAG, "도착지 주소 = $addressObject")


                var memberId: Long = App.prefs.memberId!!.toLong()
                var destination: String = addressObject.place_name
                var startAddressList: List<StartAddress> = myStartAddress.toList()
                var name: String = addressObject.user_name
                var roadAddressName: String = addressObject.road_address_name
                var addressName: String = addressObject.address_name
                var lat: Double = addressObject.lat
                var lon: Double = addressObject.lon
                var date: String = myDate

                var myBookmark = Bookmark(
                    memberId, destination, name, dateName, roadAddressName, addressName, lat, lon, date, startAddressList
                )
                Log.d(TAG, "@@@@@@@@@@@@@@")
                Log.d(TAG, "날짜날짜 = ${myBookmark.date}")
                Log.d(TAG, "@@@@@@@@@@@@@@")

                //즐겨찾기 저장 API 호출
                RetrofitManager.instance.saveBookmarkService(
                    bookmark = myBookmark,
                    completion = { responseState, responseBody ->
                        when (responseState) {

                            //API 호출 성공시
                            RESPONSE_STATE.OKAY -> {
                                Log.d(TAG, "API 호출 설공 : $responseBody")

                                //JSON parsing
                                //{}->JSONObject, []->JSONArray
                                val jsonObject = JSONObject(responseBody)
                                val statusCode = jsonObject.getInt("statusCode")

                                if (statusCode == 201) {
                                    Log.d(TAG, "@@@@@@@@@")
                                    Log.d(TAG, "성공")
                                    Log.d(TAG, "@@@@@@@@@")

                                    finish()
                                } else {
                                    Log.d(TAG, "!!!!!!!!!!!!!")
                                    Log.d(TAG, "실패")
                                    Log.d(TAG, "!!!!!!!!!!!!!")
                                }
                            }

                            //API 호출 실패시
                            RESPONSE_STATE.FAIL -> {
                                Log.d(TAG, "API 호출 실패 : $responseBody")
                            }
                        }
                    }
                )

                finish()
            } else {
                Log.d(TAG, "약속 장소가 입력되지 않았습니다.")
                Toast.makeText(this, "약속 이름 또는 약속 날짜가\n입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
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