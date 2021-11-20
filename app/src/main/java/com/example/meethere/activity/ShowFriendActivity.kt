package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.meethere.databinding.ActivityMainBinding
import com.example.meethere.databinding.ActivityShowFriendBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class ShowFriendActivity : AppCompatActivity() {
    private lateinit var binding:ActivityShowFriendBinding

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
            val intent = Intent(this, SendRequestLocationActivity::class.java)

            intent.putExtra("email", email)
            intent.putExtra("name", name)
            intent.putExtra("phone", phone)
            startActivity(intent)
        }
        binding.friendBtnRequest.setOnClickListener {
            val intent = Intent(this, SendMyLocationActivity::class.java)

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