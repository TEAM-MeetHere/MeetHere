package com.example.meethere.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.meethere.databinding.ActivityEditBookmarkBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.retrofit.request.UpdateBookmark
import com.example.meethere.utils.Constants
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_edit_bookmark.*
import org.json.JSONObject
import java.util.*

class EditBookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bookmarkId = intent.getStringExtra("bookmarkId")?.toLong()
        var promise_name: String = intent.getStringExtra("promise_name")!!
        var promise_date: String = intent.getStringExtra("promise_date")!!
        et_bookmark_name.setText(promise_name)
        et_bookmark_date.setText(promise_date)

        //캘린더 데이터
        var year: String = ""
        var month: String = ""
        var day: String = ""
        var myDate: String = promise_date

        //약속 이름
        var dateName: String = ""

        binding.etBookmarkDate.setOnClickListener {
            //캘린더
            val cal = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                Toast.makeText(this, "$y-${m + 1}-$d", Toast.LENGTH_SHORT).show()

                year = y.toString()
                month = (m + 1).toString()
                day = d.toString()

                myDate = "$year-$month-$day"
                Log.d(Constants.TAG, "약속 날짜 = $myDate")

                binding.etBookmarkDate.setText(myDate)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }

        binding.saveBtn.setOnClickListener {

            Log.d(TAG, "EditBookmarkActivity - 수정 버튼 클릭")
            dateName = binding.etBookmarkName.text.toString()

            if (dateName == "") {
                Toast.makeText(this@EditBookmarkActivity, "약속 이름을 입력해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val myUpdateBookmark = UpdateBookmark(
                bookmarkId!!, dateName, myDate
            )

            // 통신으로 bookmarkId의 myDate, dateName 만 수정하는 코드
            RetrofitManager.instance.updateBookmarkService(
                updateBookmark = myUpdateBookmark,
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
                                Toast.makeText(this@EditBookmarkActivity,
                                    message,
                                    Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                val errorMessage = jsonObjects.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@EditBookmarkActivity,
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