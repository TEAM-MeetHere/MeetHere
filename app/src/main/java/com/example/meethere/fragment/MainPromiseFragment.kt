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
import com.example.meethere.activity.ShowBookmarkActivity
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.databinding.FragmentMainPromiseBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.BookmarkObject
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
 * Use the [MainPromiseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPromiseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val bookmarkObjects = arrayListOf<BookmarkObject>()
    private val bookmarkAdapter = BookmarkAdapter(bookmarkObjects)

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
        val binding = FragmentMainPromiseBinding.inflate(inflater,container,false)

        binding.rvBookmarkList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBookmarkList.adapter = bookmarkAdapter

        bookmarkAdapter.setItemClickListener(object : BookmarkAdapter.OnItemClickListener {
            override fun onClick(
                promise_id: Long,
                addressObject: AddressObject,
                promise_name: String,
                promise_date: String,
                position: Int,
            ) {
                val intent = Intent(requireContext(), ShowBookmarkActivity::class.java)
                intent.putExtra("bookmarkId", promise_id.toString())
                intent.putExtra("addressObject", addressObject)
                intent.putExtra("promise_name", promise_name)
                intent.putExtra("promise_date", promise_date)
                startActivity(intent)
            }
        })

        refresh()

        // Inflate the layout for this fragment
        return binding.root
    }

    fun refresh() {
        bookmarkObjects.clear()
        bookmarkAdapter.notifyDataSetChanged()
        RetrofitManager.instance.bookmarkListService(
            memberId = App.prefs.memberId,
            completion = { responseState, responseBody ->
                when (responseState) {

                    //API 호출 성공시
                    RESPONSE_STATE.OKAY -> {
                        Log.d(Constants.TAG, "API 호출 성공 : $responseBody")

                        //JSON parsing
                        //{}->JSONObject, []->JSONArray
                        val jsonObject = JSONObject(responseBody)
                        val statusCode = jsonObject.getInt("statusCode")

                        if (statusCode < 400) {
                            val dataArray = jsonObject.getJSONArray("data")

                            for (i in 0..dataArray.length() - 1) {

                                val iObject = dataArray.getJSONObject(i)
                                val id = iObject.getLong("id")
                                val dateName = iObject.getString("dateName")
//                              val username = iObject.getString("username")
                                val date = iObject.getString("date")
                                val placeName = iObject.getString("placeName")
                                val roadAddressName = iObject.getString("roadAddressName")
                                val addressName = iObject.getString("addressName")
                                val lat = iObject.getString("lat")
                                val lon = iObject.getString("lon")

                                bookmarkObjects.add(
                                    BookmarkObject(
                                        id,
                                        dateName,
                                        date,
                                        placeName,
                                        roadAddressName,
                                        addressName,
                                        lat,
                                        lon
                                    )
                                )

                                Log.d(Constants.TAG, "$i 번째")
                                Log.d(Constants.TAG, "bookmarkId = $id")
                                Log.d(Constants.TAG, "약속 이름 = $dateName")
                            }

                            bookmarkAdapter.notifyDataSetChanged()
                        } else {
                            val errorMessage = jsonObject.getString("message")
                            Log.d(Constants.TAG, "error message = $errorMessage")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

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
         * @return A new instance of fragment MainPromiseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainPromiseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}