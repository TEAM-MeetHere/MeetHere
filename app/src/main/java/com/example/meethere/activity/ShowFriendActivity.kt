package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.meethere.databinding.ActivityShowFriendBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class ShowFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val friend_id = intent.getStringExtra("friend_id")

        binding.tvShowFriendEmail.text = email
        binding.tvShowFriendName.text = name
        binding.tvShowFriendPhone.text = phone

        RetrofitManager.instance.findFriendService(
            email = email!!,
            name = name!!,
            phone = phone!!,
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

                            val memberId = jsonObjects.getLong("data")

                            RetrofitManager.instance.findUserInfoService(
                                memberId = memberId,
                                completion = { responseState1, responseBody1 ->
                                    when (responseState1) {

                                        //API 호출 성공시
                                        RESPONSE_STATE.OKAY -> {
                                            Log.d(TAG, "API 호출 성공 : $responseBody1")

                                            //JSON parsing
                                            //{}->JSONObject, []->JSONArray
                                            val jsonObject1 = JSONObject(responseBody1)
                                            val statusCode1 = jsonObject1.getInt("statusCode")

                                            if (statusCode1 == 200) {
                                                val message1 = jsonObject1.getString("message")
                                                Log.d(TAG, "message1 = $message1")
                                                Toast.makeText(this, message1, Toast.LENGTH_LONG)
                                                    .show()

                                                val data = jsonObject1.getJSONObject("data")
                                                val ao = data.getJSONObject("addressObject")
                                                val road_address_name = ao.getString("roadAddressName")

                                                binding.tvAddress.text = road_address_name
                                            }
                                        }
                                        //API 호출 실패시
                                        RESPONSE_STATE.FAIL -> {
                                            Log.d(TAG, "API 호출 실패 : $responseBody1")
                                        }
                                    }
                                }

                            )
                        } else {
                            val errorMessage = jsonObjects.getString("message")
                            Log.d(TAG, "error message = $errorMessage")
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    //API 호출 실패시
                    RESPONSE_STATE.FAIL -> {
                        Log.d(TAG, "API 호출 실패 : $responseBody")
                    }
                }
            }

        )


        binding.friendBtnDelete.setOnClickListener {
            val friendId = friend_id!!.toLong()

            RetrofitManager.instance.deleteFriendService(
                friendId = friendId,
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {
                            Log.d(Constants.TAG, "API 호출 성공 : $responseBody")

                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObjects = JSONObject(responseBody)
                            val statusCode = jsonObjects.getInt("statusCode")

                            if (statusCode == 200) {
                                val message = jsonObjects.getString("message")
                                Log.d(Constants.TAG, "message = $message")
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                val errorMessage = jsonObjects.getString("message")
                                Log.d(Constants.TAG, "error message = $errorMessage")
                                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        //API 호출 실패시
                        RESPONSE_STATE.FAIL -> {
                            Log.d(Constants.TAG, "API 호출 실패 : $responseBody")
                        }
                    }
                }
            )
        }
        binding.friendBtnPosition.setOnClickListener {
            val intent = Intent(this, SendMyLocationActivity::class.java)

            intent.putExtra("email", email)
            intent.putExtra("name", name)
            intent.putExtra("phone", phone)
            startActivity(intent)
        }
        binding.friendBtnRequest.setOnClickListener {
            val intent = Intent(this, SendRequestLocationActivity::class.java)

            intent.putExtra("email", email)
            intent.putExtra("name", name)
            intent.putExtra("phone", phone)
            startActivity(intent)
        }
        binding.friendBtnShareCode.setOnClickListener {
            val intent = Intent(this, SendShareCodeActivity::class.java)

            intent.putExtra("email", email)
            intent.putExtra("name", name)
            intent.putExtra("phone", phone)
            startActivity(intent)
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