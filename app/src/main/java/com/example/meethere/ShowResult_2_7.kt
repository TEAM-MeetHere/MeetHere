package com.example.meethere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_set_location24.*
import kotlinx.android.synthetic.main.activity_show_result27.*
import kotlinx.android.synthetic.main.item_result.*
import android.content.ClipData
import android.content.ClipboardManager


class ShowResult_2_7 : AppCompatActivity() {
    private lateinit var resultAdapter: ResultAdopter

    private fun startToDetailActivity(id: Int, message: String) {
        val intent = Intent(applicationContext, ShowDetail_2_8::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result27)

        resultAdapter = ResultAdopter(mutableListOf())

        recyclerViewResult.adapter = resultAdapter
        recyclerViewResult.layoutManager = LinearLayoutManager(this)

        val resultObject1 = Result("김철수", 33)
        resultAdapter.addResult(resultObject1)
        val resultObject2 = Result("박민수", 31)
        resultAdapter.addResult(resultObject2)
        val resultObject3 = Result("최진수", 29)
        resultAdapter.addResult(resultObject3)
        val resultObject4 = Result("이상수", 38)
        resultAdapter.addResult(resultObject4)

        buttonSave.setOnClickListener {
            Toast.makeText(this@ShowResult_2_7, "즐겨찾기에 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }

        buttonShare.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", "aTHaon(임시공유코드)")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this@ShowResult_2_7, "공유 코드가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
        buttonHome.setOnClickListener {
            val intentHome = Intent(applicationContext, MainActivity::class.java)
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intentHome)
            finish()
        }

    }


}