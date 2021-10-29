package com.example.meethere.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.databinding.ActivitySaveBookmarkBinding
import com.example.meethere.utils.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SaveBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveBookmarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //캘린더 데이터
        var year: String = ""
        var month: String = ""
        var day: String = ""
        var myDate: LocalDate = LocalDate.now()

        //약속 이름
        var dateName: String = ""


        //출발, 도착 주소 리스트 받아오기
        val addresses: Array<com.example.meethere.Address> =
            intent.getSerializableExtra("addressData") as Array<com.example.meethere.Address>
        val destinationName: String? = intent.getStringExtra("destinationDataName")
        val destinationRoad: String? = intent.getStringExtra("destinationDataRoad")

        //캘린더 버튼 클릭
        binding.calendarBtn.setOnClickListener {
            //캘린더
            val cal = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()

                year = y.toString()
                month = m.toString()
                day = d.toString()

                var dateStr: String = "$year-$month-$day"
                myDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-M-d"))
                Log.d(Constants.TAG, "약속 날짜 = $myDate")

            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
        }


        //저장 버튼 클릭시
        binding.saveBtn.setOnClickListener {

            dateName = binding.dateName.text.toString()

            //약속 장소 입력되는 경우에만 진행
            if (dateName != "") {
                Toast.makeText(this@SaveBookmarkActivity, "즐겨찾기에 저장이 완료되었습니다.", Toast.LENGTH_SHORT)
                    .show()

                var startAddressList: List<String>
                for (address in addresses) {
                    Log.d(Constants.TAG, "출발지 address = $address")
                }
                Log.d(Constants.TAG, "도착지 address = $destinationName")
                Log.d(Constants.TAG, "그 외 address = $destinationRoad")

                Log.d(Constants.TAG, "약속 날짜 = $myDate")
                Log.d(Constants.TAG, "약속 이름 = $dateName")

                finish()
            }
        }
    }
}