package com.example.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.databinding.ActivityLoginBinding
import com.example.meethere.retrofit.Login
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
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
 /*
            Log.d(TAG, "LoginActivity - 검색 버튼 클릭 / currentSearchType")

            var ID: String = binding.loginId.text.toString()
            var PW: String = binding.loginPw.text.toString()
            var myLogin = Login(ID, PW)

            //로그인 API 호출
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
                            val message = jsonObject.getString("message")
                            Log.d(TAG, "statusCode = $statusCode ")
                            Log.d(TAG, "message = $message")

                            //로그인 완료
                            if (statusCode == 200 || statusCode == 201) {
                                val data = jsonObject.getJSONObject("data")
                                val id = data.getInt("id")
                                val email = data.getString("email")
                                val pw = data.getString("pw")
                                val name = data.getString("name")
                                val address = data.getString("address")
                                val phone = data.getString("phone")

                                Log.d(TAG, "memberID = $id")
                                Log.d(TAG, "memberEmail = $email")
                                Log.d(TAG, "memberPw = $pw")
                                Log.d(TAG, "memberName = $name")
                                Log.d(TAG, "memberAddress = $address")
                                Log.d(TAG, "memberPhone = $phone")
                            }

                        }
                        //API 호출 실패시
                        RESPONSE_STATE.FAIL -> {
                            Log.d(TAG, "API 호출 실패 : $responseBody")
                        }
                    }
                })
        */
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }
}