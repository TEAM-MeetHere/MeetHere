package com.example.meethere.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.meethere.databinding.ActivityAddFriendBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_add_friend.*
import org.json.JSONObject

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etFriendPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        binding.saveBtn.setOnClickListener {
            // 저장하는 코드

            val email = et_friend_email.text.toString()
            val name = et_friend_name.text.toString()
            val phone = et_friend_phone.text.toString()

            //해당 친구가 존재하는지
            //친구찾기 API 호출
            RetrofitManager.instance.findFriendService(
                email = email,
                name = name,
                phone = phone,
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {
                            Log.d(TAG, "API 호출 성공 : $responseBody")

                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObjects = JSONObject(responseBody)
                            val statusCode = jsonObjects.getInt("statusCode")

                            if (statusCode == 200) {
                                val message = jsonObjects.getString("message")
                                Log.d(TAG, "message = $message")

                                val friendId = jsonObjects.getLong("data")
                                val memberId = App.prefs.memberId

                                RetrofitManager.instance.addFriendService(
                                    memberId = memberId!!,
                                    friendId = friendId,
                                    completion = { responseState, responseBody ->
                                        when (responseState) {

                                            //API 호출 성공
                                            RESPONSE_STATE.OKAY -> {
                                                Log.d(TAG, "API 호출 성공 : $responseBody")

                                                //JSON parsing
                                                //{}->JSONObject, []->JSONArray
                                                val jsonObject2 = JSONObject(responseBody)
                                                val statusCode2 = jsonObjects.getInt("statusCode")
                                                Log.d(TAG, statusCode2.toString())
                                                if (statusCode2 == 200) {
                                                    val message2 = jsonObject2.getString("message")
                                                    Log.d(TAG, "message = $message2")
                                                    Toast.makeText(this@AddFriendActivity,
                                                        message2,
                                                        Toast.LENGTH_LONG).show()

                                                    finish()

                                                } else {
                                                    val errorMessage2 =
                                                        jsonObject2.getString("message")
                                                    Log.d(TAG, "error message = $errorMessage2")
                                                    Toast.makeText(this@AddFriendActivity,
                                                        errorMessage2,
                                                        Toast.LENGTH_LONG).show()
                                                }
                                            }
                                            //API 호출 실패
                                            RESPONSE_STATE.FAIL -> {
                                                Log.d(TAG, "API 호출 실패 : $responseBody")
                                            }
                                        }
                                    }
                                )


                            } else {
                                val errorMessage = jsonObjects.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@AddFriendActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show()
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