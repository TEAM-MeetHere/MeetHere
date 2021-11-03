package com.example.meethere.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.objects.BookmarkObject
import com.example.meethere.R
import com.example.meethere.databinding.ActivityBookmarkBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

class BookmarkActivity : AppCompatActivity() {
    private var mBinding: ActivityBookmarkBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        mBinding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //즐겨찾기 목록 받아올 리스트
        val bookmarkList = arrayListOf<BookmarkObject>()

        //즐겨찾기 목록 API 호출
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

                                bookmarkList.add(
                                    BookmarkObject(
                                        id, dateName, "멤버", "날짜", "목적지"
                                    )
                                )

                                Log.d(TAG, "$i 번째")
                                Log.d(TAG, "bookmarkId = $id")
                                Log.d(TAG, "약속 이름 = $dateName")
                            }

                            binding.re.layoutManager =
                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            binding.re.setHasFixedSize(true)
                            binding.re.adapter = BookmarkAdapter(bookmarkList)
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
}
