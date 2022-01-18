package com.choitaek.meethere.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.choitaek.meethere.activity.ShowFriendActivity
import com.choitaek.meethere.adapter.FriendListAdapter
import com.choitaek.meethere.databinding.FragmentMainFriendBinding
import com.choitaek.meethere.objects.FriendObject
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants
import com.choitaek.meethere.utils.RESPONSE_STATE
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

    private val friendObjects = arrayListOf<FriendObject>()

    private lateinit var binding: FragmentMainFriendBinding

    private val friendListAdapter = FriendListAdapter(friendObjects)

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
        binding = FragmentMainFriendBinding.inflate(inflater, container, false)

        binding.rvFriendList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFriendList.adapter = friendListAdapter

        friendListAdapter.setItemClickListener(object : FriendListAdapter.OnItemClickListener {
            override fun onClick(friendObject: FriendObject, position: Int) {
                val intent = Intent(requireContext(), ShowFriendActivity::class.java)
                val friendEmail = friendObject.friend_email
                val friendName = friendObject.friend_name
                val friendPhone = friendObject.friend_phone
                val friendId = friendObject.friend_id

                intent.putExtra("email", friendEmail)
                intent.putExtra("name", friendName)
                intent.putExtra("phone", friendPhone)
                intent.putExtra("friend_id", friendId)
                startActivity(intent)
            }
        })

        refresh()

        // Inflate the layout for this fragment
        return binding.root
    }

    fun refresh() {
        friendObjects.clear()
        
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
                            Log.d(Constants.TAG, "친구 = $message")

                            val dataArray = jsonObjects.getJSONArray("data")

                            for (i in 0..dataArray.length() - 1) {

                                val iObject = dataArray.getJSONObject(i)
                                val friendId = iObject.getLong("id")
                                val email = iObject.getString("email")
                                val name = iObject.getString("name")
                                val phone = iObject.getString("phone")

                                val friend = FriendObject(friendId, name, email, phone)
                                friendObjects.add(friend)
                            }
                            friendListAdapter.notifyDataSetChanged()

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