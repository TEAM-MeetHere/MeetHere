package com.example.meethere.activity

import android.content.Intent
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.databinding.ActivityShowBookmarkBinding
import com.example.meethere.fragment.MainPromiseFragment
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.BookmarkObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class ShowBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowBookmarkBinding
    lateinit var bookmarkObjects: MutableList<BookmarkObject>
    lateinit var bookmarkAdapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShowBookmarkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bookmarkObjects = intent.getSerializableExtra("bookmarkData") as MutableList<BookmarkObject>
        bookmarkAdapter = BookmarkAdapter(bookmarkObjects)

        binding.rvBookmarkList.layoutManager =
            LinearLayoutManager(this@ShowBookmarkActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvBookmarkList.adapter = bookmarkAdapter

        if (intent.hasExtra("date")) {
            val bookmarkIter = bookmarkObjects.listIterator()
            val dateString = intent.getStringExtra("date")
            binding.tvShowBookmark.text = dateString + " 일정"
            while (bookmarkIter.hasNext()) {
                val nextString = bookmarkIter.next().promise_date
                if (nextString != dateString) {
                    Log.d("테스트", "$nextString == $dateString")
                    bookmarkIter.remove()
                }
            }
        } else binding.tvShowBookmark.text = "전체 일정"

        bookmarkAdapter.notifyDataSetChanged()
        Log.d("테스트 : 북마크개수",bookmarkObjects.size.toString())
        var addressObjectsArray = ArrayList<ArrayList<AddressObject>>()

        for (i in 0 until bookmarkObjects.size) {
            RetrofitManager.instance.findStartAddressListService(
                bookmarkId = bookmarkObjects[i].promise_id!!,
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {
                            Log.d(TAG, "API 호출 성공 : $responseBody")

                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if (statusCode == 200) {
                                val message = jsonObject.getString("message")
                                val dataArray = jsonObject.getJSONArray("data")

                                val addressObjects = ArrayList<AddressObject>()

                                for (i in 0..dataArray.length() - 1) {
                                    val iObject = dataArray.getJSONObject(i)
                                    val placeName = iObject.getString("placeName")
                                    val username = iObject.getString("username")
                                    val roadAddressName = iObject.getString("roadAddressName")
                                    val addressName = iObject.getString("addressName")
                                    val lat = iObject.getDouble("lat")
                                    val lon = iObject.getDouble("lon")

                                    addressObjects.add(AddressObject(
                                        placeName,
                                        username,
                                        roadAddressName,
                                        addressName,
                                        lat,
                                        lon)
                                    )

                                    Log.d(TAG, "$i 번째 출발 주소")
                                    Log.d(TAG, "placeName = $placeName")
                                    Log.d(TAG, "username = $username")
                                    Log.d(TAG, "roadAddressName = $roadAddressName")
                                    Log.d(TAG, "addressName = $addressName")
                                    Log.d(TAG, "lat = $lat")
                                    Log.d(TAG, "lon = $lon")
                                }

                                addressObjectsArray.add(addressObjects)
                            } else {
                                val message = jsonObject.getString("message")
                                Log.d(TAG, "response message = $message")
                                Toast.makeText(this@ShowBookmarkActivity,
                                    message,
                                    Toast.LENGTH_SHORT)
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

        bookmarkAdapter.setItemClickListener(object : BookmarkAdapter.OnItemClickListener {
            override fun onClick(
                promise_id: Long,
                addressObject: AddressObject,
                promise_name: String,
                promise_date: String,
                position: Int,
            ) {
                val intent =
                    Intent(this@ShowBookmarkActivity, ShowBookmarkResultActivity::class.java)
                val addressObjects: Array<AddressObject> = addressObjectsArray[position].toTypedArray()
                intent.putExtra("addressData", addressObjects)
                intent.putExtra("bookmarkId", promise_id.toString())
                intent.putExtra("addressObject", addressObject)
                intent.putExtra("promise_name", promise_name)
                intent.putExtra("promise_date", promise_date)
                startActivity(intent)
                finish()
            }
        })
    }
}