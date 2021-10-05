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

        val resultObject1 = Result("임영택",10)
        resultAdapter.addResult(resultObject1)
        val resultObject2 = Result("최규림",100)
        resultAdapter.addResult(resultObject2)
        val resultObject3 = Result("최현호",120)
        resultAdapter.addResult(resultObject3)

        buttonSave.setOnClickListener {
            Toast.makeText(this@ShowResult_2_7, "저장하기", Toast.LENGTH_SHORT).show()
        }

        buttonShare.setOnClickListener {
            Toast.makeText(this@ShowResult_2_7, "공유하기", Toast.LENGTH_SHORT).show()
        }

    }


}