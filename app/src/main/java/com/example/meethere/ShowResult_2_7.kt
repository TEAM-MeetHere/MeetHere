package com.example.meethere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_show_result27.*

class ShowResult_2_7 : AppCompatActivity() {
    private lateinit var resultAdapter: ResultAdopter

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
    }
}