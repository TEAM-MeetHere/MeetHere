package com.example.meethere.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import com.example.meethere.R
import com.example.meethere.databinding.ActivityCalendarBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.BookmarkObject
import com.example.meethere.utils.Constants.TAG
import java.io.FileInputStream

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding

    lateinit var calendarView: CalendarView
    lateinit var diaryTextView: TextView
    lateinit var diaryContent:TextView
    lateinit var title:TextView
    lateinit var contextEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookmarkObjects: ArrayList<BookmarkObject> =
            intent.getSerializableExtra("bookmarkObjects") as ArrayList<BookmarkObject>

        for (bo in bookmarkObjects) {
            Log.d(TAG, "임영택ㄱ = ${bo.promise_date}")
        }

        // UI값 생성
        calendarView=findViewById(R.id.calendarView)
        diaryTextView=findViewById(R.id.diaryTextView)
        diaryContent=findViewById(R.id.diaryContent)
        title=findViewById(R.id.title)
        contextEditText=findViewById(R.id.contextEditText)

        title.text = "약속 달력"

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            diaryTextView.visibility = View.VISIBLE
            contextEditText.visibility = View.VISIBLE
            diaryContent.visibility = View.INVISIBLE
            diaryTextView.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            contextEditText.setText("")
        }
    }
}