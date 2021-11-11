package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.R
import com.example.meethere.adapter.FriendAdapter
import com.example.meethere.databinding.ActivityFriendBinding
import com.example.meethere.objects.FriendObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class FriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendBinding
    private val listItems = arrayListOf<FriendObject>()
    private val friendAdapter = FriendAdapter(listItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFriendList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFriendList.adapter = friendAdapter

        //sms 실시간 위치 요청
        friendAdapter.setItemClickListener(object : FriendAdapter.OnItemClickListener {
            override fun onClick(friendObject: FriendObject, position: Int) {
                val intent = Intent(this@FriendActivity, SendRequestLocationActivity::class.java)
                val friendEmail = friendObject.friend_email
                val friendName = friendObject.friend_name
                val friendPhone = friendObject.friend_phone

                intent.putExtra("email", friendEmail)
                intent.putExtra("name", friendName)
                intent.putExtra("phone", friendPhone)
                startActivity(intent)
            }
        })

        //sms 공유코드 전송
        friendAdapter.setItemClickListener2(object : FriendAdapter.OnItemClickListener {
            override fun onClick(friendObject: FriendObject, position: Int) {
                val intent = Intent(this@FriendActivity, SendShareCodeActivity::class.java)
                val friendEmail = friendObject.friend_email
                val friendName = friendObject.friend_name
                val friendPhone = friendObject.friend_phone

                intent.putExtra("email", friendEmail)
                intent.putExtra("name", friendName)
                intent.putExtra("phone", friendPhone)
                startActivity(intent)
            }
        })

        //내 위치 전송
        friendAdapter.setItemClickListener3(object :FriendAdapter.OnItemClickListener{
            override fun onClick(friendObject: FriendObject, position: Int) {
                val intent = Intent(this@FriendActivity, SendMyLocationActivity::class.java)
                val friendEmail = friendObject.friend_email
                val friendName = friendObject.friend_name
                val friendPhone = friendObject.friend_phone

                intent.putExtra("email", friendEmail)
                intent.putExtra("name", friendName)
                intent.putExtra("phone", friendPhone)
                startActivity(intent)
            }
        })

        RetrofitManager.instance.friendListService(
            memberId = App.prefs.memberId!!,
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

                            val dataArray = jsonObjects.getJSONArray("data")

                            for (i in 0..dataArray.length() - 1) {

                                val iObject = dataArray.getJSONObject(i)
                                val friendId = iObject.getLong("id")
                                val email = iObject.getString("email")
                                val name = iObject.getString("name")
                                val phone = iObject.getString("phone")

                                val friend = FriendObject(friendId, name, email, phone)
                                listItems.add(friend)
                            }
                            friendAdapter.notifyDataSetChanged()

                        } else {
                            val errorMessage = jsonObjects.getString("message")
                            Log.d(TAG, "error message = $errorMessage")
                            Toast.makeText(this@FriendActivity, errorMessage, Toast.LENGTH_LONG)
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

        binding.buttonAddFriend.setOnClickListener() {
            val intent = Intent(this@FriendActivity, AddFriendActivity::class.java)
            startActivity(intent)
        }
    }
}