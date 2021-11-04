package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.meethere.*
import com.example.meethere.objects.AddressObject
import com.example.meethere.databinding.ActivityMainBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setText("안녕하세요 " + App.prefs.username + "님")

        Log.d("임영택 Main Activity lat pref",App.prefs.lat!!)
        Log.d("임영택 Main Activity lon pref",App.prefs.lon!!)

        //로그아웃 클릭 시
        binding.logoutBtn.setOnClickListener {
            App.prefs.username = null
            App.prefs.email = null
            App.prefs.token = null
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //처음 오셨다면? 클릭 시
        binding.newBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_new, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()
            val okButton = mDialogView.findViewById<Button>(R.id.activity_new_btn)
            okButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        //공유코드 입력 버튼 클릭시
        binding.codeBtn.setOnClickListener {

            val mCodeView = LayoutInflater.from(this).inflate(R.layout.activity_input_code, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mCodeView)

            val mAlertDialog = mBuilder.show()
            val okButton = mCodeView.findViewById<Button>(R.id.input_code_btn)
            okButton.setOnClickListener {

                val random_code = mCodeView.findViewById<EditText>(R.id.input_code_text).text.toString()
                Log.d(TAG, "입력된 랜덤코드 = $random_code")
                //공유코드(도착지점) 불러오기 API 호출
                RetrofitManager.instance.shareDestinationService(
                    code = random_code,
                    completion = {responseState, responseBody ->
                        when(responseState){

                            //API 호출 성공시
                            RESPONSE_STATE.OKAY->{
                                Log.d(TAG, "API 호출 성공 : $responseBody")

                                //JSON parsing
                                //{}->JSONObject, []->JSONArray
                                val jsonObject = JSONObject(responseBody)
                                val statusCode = jsonObject.getInt("statusCode")

                                if (statusCode == 200) {
                                    val message = jsonObject.getString("message")
                                    Log.d(TAG, "message = $message")
                                    val data = jsonObject.getJSONObject("data")

                                    val shareId = data.getLong("id")
                                    val placeName = data.getString("placeName")
                                    val username = data.getString("username")
                                    val roadAddressName = data.getString("roadAddressName")
                                    val addressName = data.getString("addressName")
                                    val lat = data.getDouble("lat")
                                    val lon = data.getDouble("lon")

                                    Log.d(TAG, "shareID = $shareId")
                                    Log.d(TAG, "placeName = $placeName")
                                    Log.d(TAG, "username = $username")
                                    Log.d(TAG, "roadAddressName = $roadAddressName")
                                    Log.d(TAG, "addressName = $addressName")
                                    Log.d(TAG, "lat = $lat")
                                    Log.d(TAG, "lon = $lon")

                                    var addressObject = AddressObject(placeName, username, roadAddressName, addressName, lat, lon)
                                    var addressObjects = ArrayList<AddressObject>()

                                    //공유코드(출발지점 리스트) 불러오기 API 호출
                                    RetrofitManager.instance.shareStartService(
                                        shareId = shareId,
                                        completion = {responseState, responseBody ->
                                            when(responseState){
                                                //API 호출 성공시
                                                RESPONSE_STATE.OKAY->{
                                                    Log.d(TAG, "API 호출 성공 : $responseBody")

                                                    //JSON parsing
                                                    //{}->JSONObject, []->JSONArray
                                                    val jsonObject = JSONObject(responseBody)
                                                    val statusCode = jsonObject.getInt("statusCode")

                                                    if (statusCode == 200) {
                                                        val dataArray = jsonObject.getJSONArray("data")
                                                        for (i in 0..dataArray.length() - 1) {

                                                            val iObject = dataArray.getJSONObject(i)

                                                            val placeName = iObject.getString("placeName")
                                                            val username = iObject.getString("username")
                                                            val roadAddressName = iObject.getString("roadAddressName")
                                                            val addressName = iObject.getString("addressName")
                                                            val lat = iObject.getDouble("lat")
                                                            val lon = iObject.getDouble("lon")

                                                            addressObjects.add(AddressObject(placeName, username, roadAddressName, addressName, lat, lon))
                                                        }

                                                        val intent = Intent(this, ShowResultActivity::class.java)
                                                        intent.putExtra("addressData", addressObjects.toArray(arrayOfNulls<AddressObject>(addressObjects.size)))
                                                        intent.putExtra("addressObject", addressObject)
                                                        startActivity(intent)
                                                        mAlertDialog.dismiss()

                                                    } else {
                                                        var errorMessage = jsonObject.getString("message")
                                                        Log.d(TAG, "error message = $errorMessage")
                                                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                                //API 호출 실패시
                                                RESPONSE_STATE.FAIL->{
                                                    Log.d(TAG, "API 호출 실패 : $responseBody")
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    val errorMessage = jsonObject.getString("message")
                                    Log.d(TAG, "error message = $errorMessage")
                                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
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
        }

        //정보 수정 버튼 클릭시
        edit_btn.setOnClickListener({
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        })

        //즐겨찾기 목록 버튼 클릭시
        bookmark_btn.setOnClickListener({
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        })

        //장소 검색 시작 버튼 클릭시
        search_btn.setOnClickListener({
            val intent = Intent(this, SetLocationActivity::class.java)
            startActivity(intent)
        })

        //TEST
        //즐겨찾기의 출발지 지점 리스트 확인
        binding.startListBtn.setOnClickListener {
            val intent = Intent(this, ShowBookmarkActivity::class.java)

            //ShowBookmarkActivity에서 getLongExtra로 받을 수가 없어서
            //String으로 받고 ShowBookmarkActivity에서 Long으로 변환
            intent.putExtra("bookmarkId", "32")
            startActivity(intent)
        }
    }
}
