package com.example.meethere.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.R
import com.example.meethere.activity.AddFriendActivity
import com.example.meethere.activity.SendRequestLocationActivity
import com.example.meethere.activity.SendShareCodeActivity
import com.example.meethere.adapter.FriendAdapter
import com.example.meethere.databinding.FragmentMainFriendBinding
import com.example.meethere.objects.FriendObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFriendFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val listItems = arrayListOf<FriendObject>()
    private val friendAdapter = FriendAdapter(listItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentMainFriendBinding.inflate(inflater, container, false)

        binding.rvFriendList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFriendList.adapter = friendAdapter

        //sms 실시간 위치 요청
        friendAdapter.setItemClickListener(object : FriendAdapter.OnItemClickListener {
            override fun onClick(friendObject: FriendObject, position: Int) {
                val intent = Intent(requireContext(), SendRequestLocationActivity::class.java)
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
                val intent = Intent(requireContext(), SendShareCodeActivity::class.java)
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
                            friendAdapter.notifyDataSetChanged()

                        } else {
                            val errorMessage = jsonObjects.getString("message")
                            Log.d(Constants.TAG, "error message = $errorMessage")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
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
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFriendFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFriendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}