package com.choitaek.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.choitaek.meethere.databinding.ActivityFindIdBinding
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class FindIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //아이디 찾기 버튼 클릭시
        binding.findIDBtn.setOnClickListener {
            Log.d(TAG, "FindIDActivity - 아이디 찾기 버튼 클릭 / currentSearchType")

            var NAME: String = binding.findIDName.text.toString()
            var PHONE: String = binding.findIDPhone.text.toString()

            Log.d(TAG, "input name = $NAME")
            Log.d(TAG, "input phone = $PHONE")

            //아이디 찾기 API 호출
            RetrofitManager.instance.findIdService(
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

                            if(statusCode == 200){
                                val message = jsonObject.getString("message")
                                val findEmail = jsonObject.getString("data")

                                Toast.makeText(this, message, Toast.LENGTH_SHORT)
                                    .show()

                                Log.d(TAG, "findEmail = $findEmail")

                                //아이디 찾기 버튼 안보이게
                                binding.findIDBtn.visibility = View.INVISIBLE

                                //이메일 공개
                                binding.emailInfo.setText(findEmail)
                                binding.emailInfo.visibility = View.VISIBLE

                                //로그인 페이지로 이동 버튼 공개
                                binding.moveToLoginPage.visibility=View.VISIBLE

                                //버튼 클릭시, 로그인 화면으로 이동
                                binding.moveToLoginPage.setOnClickListener {
                                    var intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }

                            }else{
                                val errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@FindIDActivity, errorMessage, Toast.LENGTH_SHORT)
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