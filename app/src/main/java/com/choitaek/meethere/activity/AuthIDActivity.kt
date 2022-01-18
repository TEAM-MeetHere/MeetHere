package com.choitaek.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.choitaek.meethere.databinding.ActivityAuthIdBinding
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.retrofit.request.Verify
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class AuthIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 버튼 클릭
        binding.authIDRegister.setOnClickListener {
            Log.d(TAG, "AuthIDActivity - 인증 버튼 클릭 / currentSearchType")

            var inputEmail = intent.getStringExtra("inputEmail").toString()
            var authCode = binding.authIDCode.text.toString().toInt()
            var myVerify = Verify(inputEmail, authCode)

            RetrofitManager.instance.verifyService(
                verify = myVerify,
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
                                val message = jsonObject.getString("message")
                                Log.d(TAG, "response Message = $message")

                                Toast.makeText(this@AuthIDActivity, message, Toast.LENGTH_SHORT)
                                    .show()

                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }else{
                                val message = jsonObject.getString("message")
                                Log.d(TAG, "response Message = $message")

                                Toast.makeText(this@AuthIDActivity, message, Toast.LENGTH_SHORT)
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
}