package com.choitaek.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.choitaek.meethere.R
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.retrofit.request.Login
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants
import com.choitaek.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (App.prefs.email != "" && App.prefs.pw != "") {

            var ID: String = App.prefs.email.toString()
            var PW: String = App.prefs.pw.toString()
            Log.d("before id ", ID)
            Log.d("before pw ", PW)
            var myLogin = Login(ID, PW)

            //로그인 API 호출
            LOGIN_API(myLogin)
        } else {
            Handler().postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }, DURATION)
        }
    }

    private fun LOGIN_API(myLogin: Login) {
        RetrofitManager.instance.loginService(
            login = myLogin,
            completion = { responseState, responseBody ->
                when (responseState) {

                    //API 호출 성공시
                    RESPONSE_STATE.OKAY -> {
                        Log.d(Constants.TAG, "API 호출 성공 : $responseBody")

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
                            Log.d(Constants.TAG, "error message = $errorMessage")
                            Toast.makeText(this@SplashActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    //API 호출 실패시
                    RESPONSE_STATE.FAIL -> {
                        Log.d(Constants.TAG, "API 호출 실패 : $responseBody")
                    }
                }
            })
    }


    companion object {
        private const val DURATION: Long = 2000
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

