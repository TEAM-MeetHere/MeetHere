package com.example.meethere.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.FriendListAdapter
import com.example.meethere.databinding.ActivityFriendListBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.FriendObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding

    private val listItems = arrayListOf<FriendObject>()
    private val friendListAdapter = FriendListAdapter(listItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFriendList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFriendList.adapter = friendListAdapter

        friendListAdapter.setItemClickListener(object : FriendListAdapter.OnItemClickListener {
            override fun onClick(friendObject: FriendObject, position: Int) {

                val friendEmail = friendObject.friend_email
                val friendName = friendObject.friend_name
                val friendPhone = friendObject.friend_phone

                RetrofitManager.instance.findFriendService(
                    email = friendEmail,
                    name = friendName,
                    phone = friendPhone,
                    completion = {responseState, responseBody ->
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
                                        completion = {responseState1, responseBody1 ->
                                            when (responseState1) {

                                                //API 호출 성공
                                                RESPONSE_STATE.OKAY->{
                                                    Log.d(TAG, "API 호출 성공 : $responseBody1")

                                                    //JSON parsing
                                                    //{}->JSONObject, []->JSONArray
                                                    val jsonObject1 = JSONObject(responseBody1)
                                                    val statusCode1 = jsonObject1.getInt("statusCode")

                                                    if (statusCode1 == 200) {
                                                        val message1 = jsonObject1.getString("message")
                                                        Log.d(TAG, "message1 = $message1")
                                                        Toast.makeText(this@FriendListActivity, message, Toast.LENGTH_LONG).show()

                                                        val data = jsonObject1.getJSONObject("data")
                                                        val ao = data.getJSONObject("addressObject")
                                                        val place_name = ao.getString("placeName")
                                                        val user_name = friendName
                                                        val road_address_name = ao.getString("roadAddressName")
                                                        val address_name = ao.getString("addressName")
                                                        val lat = ao.getDouble("lat")
                                                        val lon = ao.getDouble("lon")

                                                        val addressObject = AddressObject(place_name, user_name, road_address_name, address_name, lat, lon)
                                                        val intent = intent
                                                        intent.putExtra("addressObject", addressObject)
                                                        // 오브젝트를 이전 액티비티로 전달해줌
                                                        setResult(Activity.RESULT_OK, intent)
                                                        finish()

                                                    } else {
                                                        val errorMessage1 = jsonObject1.getString("message")
                                                        Log.d(TAG, "error message = $errorMessage1")
                                                        Toast.makeText(this@FriendListActivity, errorMessage1, Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                                //API 호출 실패
                                                RESPONSE_STATE.FAIL->{
                                                    Log.d(TAG, "API 호출 실패 : $responseBody1")
                                                }
                                            }
                                        }
                                    )


                                } else {
                                    val errorMessage = jsonObjects.getString("message")
                                    Log.d(TAG, "error message = $errorMessage")
                                    Toast.makeText(this@FriendListActivity,
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
        })

        RetrofitManager.instance.friendListService(
            memberId = App.prefs.memberId!!,
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

                            friendListAdapter.notifyDataSetChanged()

                        } else {
                            val errorMessage = jsonObjects.getString("message")
                            Log.d(Constants.TAG, "error message = $errorMessage")
                            Toast.makeText(this@FriendListActivity, errorMessage, Toast.LENGTH_LONG)
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