package com.choitaek.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.choitaek.meethere.databinding.ActivityLoginBinding
import com.choitaek.meethere.retrofit.request.Login
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //회원가입 클릭
        binding.join.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //아이디 찾기 클릭
        binding.findId.setOnClickListener {
            val intent = Intent(this, FindIDActivity::class.java)
            startActivity(intent)
        }

        //비밀번호 찾기 클릭
        binding.findPw.setOnClickListener {
            val intent = Intent(this, FindPWActivity::class.java)
            startActivity(intent)
        }

        //로그인 클릭
        binding.loginBtn.setOnClickListener {

            Log.d(TAG, "LoginActivity - 검색 버튼 클릭 / currentSearchType")

            var ID: String = binding.loginId.text.toString()
            var PW: String = binding.loginPw.text.toString()
            var myLogin = Login(ID, PW)

            //로그인 API 호출
            LOGIN_API(myLogin)
        }

    }

    private fun LOGIN_API(myLogin: Login) {
        RetrofitManager.instance.loginService(
            login = myLogin,
            completion = { responseState, responseBody ->
                when (responseState) {

                    //API 호출 성공시
                    RESPONSE_STATE.OKAY -> {
                        Log.d(TAG, "API 호출 성공 : $responseBody")

                        //JSON parsing
                        //{}->JSONObject, []->JSONArray
                        val jsonObject = JSONObject(responseBody)
                        val statusCode = jsonObject.getInt("statusCode")

                        if (statusCode == 200) {
                            val token = jsonObject.getString("token")
                            App.prefs.token = token

                            val email = jsonObject.getString("email")
                            App.prefs.email = email

                            App.prefs.pw = myLogin.password

                            val username = jsonObject.getString("username")
                            App.prefs.username = username

                            val memberId = jsonObject.getLong("memberId")
                            App.prefs.memberId = memberId

                            val phone = jsonObject.getString("phone")
                            App.prefs.phone = phone

                            val addressObject = jsonObject.getJSONObject("addressObject")

                            val place_name = addressObject.getString("placeName")
                            App.prefs.place_name = place_name

                            val road_address_name = addressObject.getString("roadAddressName")
                            App.prefs.road_address_name = road_address_name

                            val address_name = addressObject.getString("addressName")
                            App.prefs.address_name = address_name

                            val lat = addressObject.getString("lat")
                            App.prefs.lat = lat

                            val lon = addressObject.getString("lon")
                            App.prefs.lon = lon

                            val intent = Intent(this, MainNewActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val errorMessage = jsonObject.getString("message")
                            Log.d(TAG, "error message = $errorMessage")
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    //API 호출 실패시
                    RESPONSE_STATE.FAIL -> {
                        Log.d(TAG, "API 호출 실패 : $responseBody")
                    }
                }
            })
    }
}