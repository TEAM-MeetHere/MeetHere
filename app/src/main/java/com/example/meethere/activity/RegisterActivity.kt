package com.example.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.R
import com.example.meethere.databinding.ActivityRegisterBinding
import com.example.meethere.retrofit.Register
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var ID: String = binding.registerEmail.text.toString()
        var PW: String = binding.registerPw.text.toString()
        var RE_PW: String = binding.registerRewritePw.text.toString()
        var NAME: String = binding.registerName.text.toString()
        var ADDRESS: String = binding.registerAddress.text.toString()
        var PHONE: String = binding.registerPhone.text.toString()
        var SNS_TYPE: String = "WWG"

        var myRegister=Register(ID,PW,RE_PW,NAME,ADDRESS,PHONE)

        //회원가입 API 호출
        RetrofitManager.instance.registerService(
            register = myRegister,
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
                            val inputEmail = jsonObject.getString("email")
                            Log.d(TAG, "inputEmail = $inputEmail")

                            //인증 단계로 이동
                            auth_ID.setOnClickListener({
                                var intent = Intent(this, AuthIDActivity::class.java)
                                intent.putExtra("inputEmail", inputEmail)
                                startActivity(intent)
                            })
                        }else{
                            val errorMessage = jsonObject.getString("message")
                            Log.d(TAG, "error message = $errorMessage")
                            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
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