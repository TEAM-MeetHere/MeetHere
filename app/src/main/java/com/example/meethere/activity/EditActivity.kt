package com.example.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.R
import com.example.meethere.databinding.ActivityEditBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.retrofit.request.Update
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editEmail.setText(intent.getStringExtra("email").toString())
        binding.editName.setText(intent.getStringExtra("name").toString())
        binding.editAddress.setText(intent.getStringExtra("address").toString())
        binding.editPhone.setText(intent.getStringExtra("phone").toString())

        //수정 버튼 클릭시
        binding.editInfoButton.setOnClickListener {
            Log.d(TAG, "EditActivity - 수정 버튼 클릭 / currentSearchType")

            var memberId: Long = App.prefs.memberId!!
            var pw1: String? = binding.editPw.text.toString()
            var pw2: String? = binding.editRewritePw.text.toString()
            var name: String? = binding.editName.text.toString()
            var address: String? = binding.editAddress.text.toString()
            var phone: String? = binding.editPhone.text.toString()
            var myUpdate = Update(memberId, pw1!!, pw2!!, name!!, address!!, phone!!)

            RetrofitManager.instance.updateService(
                update = myUpdate,
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {
                            Log.d(TAG, "API 호출 성공 : $responseBody")
                            Log.d(TAG, "pw1 = $pw1")
                            Log.d(TAG, "pw2 = $pw2")
                            Log.d(TAG, "name = $name")
                            Log.d(TAG, "address = $address")
                            Log.d(TAG, "phone = $phone")
                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if (statusCode == 200) {
                                App.prefs.username = name
                                App.prefs.address = address
                                App.prefs.phone = phone

                                var message = jsonObject.getString("message")
                                Log.d(TAG, "message = $message")
                                Toast.makeText(this@EditActivity, message, Toast.LENGTH_SHORT)
                                    .show()

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                var errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@EditActivity, errorMessage, Toast.LENGTH_SHORT)
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