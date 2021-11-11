package com.example.meethere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.objects.BookmarkObject
import com.example.meethere.R
import com.example.meethere.databinding.ActivityBookmarkBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.item_bookmark.*
import org.json.JSONObject

class BookmarkActivity : AppCompatActivity() {
    private var mBinding: ActivityBookmarkBinding? = null
    private val binding get() = mBinding!!

    private val bookmarkObjects = arrayListOf<BookmarkObject>()
    private val bookmarkAdapter = BookmarkAdapter(bookmarkObjects)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        mBinding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvBookmarkList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBookmarkList.adapter = bookmarkAdapter

        bookmarkAdapter.setItemClickListener(object : BookmarkAdapter.OnItemClickListener {
            override fun onClick(
                promise_id: Long,
                addressObject: AddressObject,
                promise_name: String,
                promise_date: String,
                position: Int,
            ) {
                val intent = Intent(this@BookmarkActivity, ShowBookmarkActivity::class.java)
                intent.putExtra("bookmarkId", promise_id.toString())
                intent.putExtra("addressObject", addressObject)
                intent.putExtra("promise_name", promise_name)
                intent.putExtra("promise_date", promise_date)
                startActivity(intent)
            }
        })
        
        binding.button.setOnClickListener {
            val intent = Intent(this@BookmarkActivity, CalendarActivity::class.java)
            intent.putExtra("bookmarkObjects", bookmarkObjects)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        bookmarkObjects.clear()
        bookmarkAdapter.notifyDataSetChanged()
        RetrofitManager.instance.bookmarkListService(
            memberId = App.prefs.memberId,
            completion = { responseState, responseBody ->
                when (responseState) {

                    //API 호출 성공시
                    RESPONSE_STATE.OKAY -> {
                        Log.d(TAG, "API 호출 성공 : $responseBody")

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

                                Log.d(TAG, "$i 번째")
                                Log.d(TAG, "bookmarkId = $id")
                                Log.d(TAG, "약속 이름 = $dateName")
                            }

                            bookmarkAdapter.notifyDataSetChanged()
                        } else {
                            val errorMessage = jsonObject.getString("message")
                            Log.d(TAG, "error message = $errorMessage")
                            Toast.makeText(this@BookmarkActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    RESPONSE_STATE.FAIL -> {
                        Log.d(TAG, "API 호출 실패 : $responseBody")
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
