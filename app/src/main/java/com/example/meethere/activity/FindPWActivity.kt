package com.example.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.R
import com.example.meethere.databinding.ActivityFindPwBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class FindPWActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFindPwBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //비밀번호 찾기 버튼 클릭시
        binding.findPWBtn.setOnClickListener {
            Log.d(TAG, "FindPwActivity - 비밀번호 찾기 버튼 클릭 / currentSearchType")

            var EMAIL:String=binding.findPWEmail.text.toString()
            var NAME:String=binding.findPWName.text.toString()
            var PHONE:String=binding.findPWPhone.text.toString()

            //비밀번호 찾기 API 호출
            RetrofitManager.instance.findPwService(
                email = EMAIL,
                name = NAME,
                phone = PHONE,
                completion = {responseState, responseBody ->
                    when(responseState){

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY->{
                            Log.d(TAG, "API 호출 성공 : $responseBody")

                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if(statusCode==200){
                                val message = jsonObject.getString("message")
                                val data = jsonObject.getString("data")

                                Toast.makeText(this, message, Toast.LENGTH_SHORT)
                                    .show()

                                Log.d(TAG, "data = $data")

                                //비밀번호 찾기 버튼 안보이게
                                binding.findPWBtn.visibility = View.INVISIBLE

                                //비밀번호 갱신 메시지 공개
                                binding.sendEmailMessage.setText(data)
                                binding.sendEmailMessage.visibility = View.VISIBLE

                                //로그인 페이지로 이동 버튼 공개
                                binding.moveToLogin.visibility = View.VISIBLE

                                //버튼 클릭시, 로그인 화면으로 이동
                                binding.moveToLogin.setOnClickListener {
                                    var intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }

                            }else{
                                val errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@FindPWActivity, errorMessage, Toast.LENGTH_SHORT)
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
}