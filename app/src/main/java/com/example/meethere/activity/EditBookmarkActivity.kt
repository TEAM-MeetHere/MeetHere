package com.example.meethere.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.meethere.R
import com.example.meethere.databinding.ActivityEditBookmarkBinding
import com.example.meethere.utils.Constants
import kotlinx.android.synthetic.main.activity_edit_bookmark.*
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
            dateName = binding.etBookmarkName.text.toString()

            // 통신으로 bookmarkId의 myDate, dateName 만 수정하는 코드
        }
    }
}