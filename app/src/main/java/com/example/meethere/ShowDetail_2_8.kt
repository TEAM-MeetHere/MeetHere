package com.example.meethere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_show_detail28.*

class ShowDetail_2_8 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail28)

        val intent = intent
        val Name = intent.getStringExtra("Name")

        textViewNameDetail.text = Name + " 님의 상세 경로"

        btnShowMap.setOnClickListener {
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            val intent = Intent(applicationContext, ShowDetailMap_2_9::class.java)
            startActivity(intent)
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