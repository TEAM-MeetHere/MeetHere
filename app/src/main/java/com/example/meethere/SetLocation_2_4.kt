package com.example.meethere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_set_location24.*

class SetLocation_2_4 : AppCompatActivity() {
    private lateinit var addressAdapter: AddressAdopter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_location24)
        addressAdapter = AddressAdopter(mutableListOf())


        recyclerViewAddress.adapter = addressAdapter
        recyclerViewAddress.layoutManager = LinearLayoutManager(this)

        val address1 = "인천광역시 미추홀구 인하로 100"
        val name1 = "김철수"
        val addressObject1 = Address(address1,name1)
        val address2 = "경기 부천시 부일로469번길 28"
        val name2 = "박민수"
        val addressObject2 = Address(address2,name2)
        val address3 = "인천광역시 미추홀구 인하로 100"
        val name3 = "최진수"
        val addressObject3 = Address(address3,name3)

        addressAdapter.addAddress(addressObject1)
        addressAdapter.addAddress(addressObject2)
        addressAdapter.addAddress(addressObject3)


        btnAdd.setOnClickListener {
            /* val address = textViewAddress.text.toString()
             val name = textViewName.text.toString()*/
            val address = "인천광역시 미추홀구 인하로 100"
            val name = "임영택"
            val addressObject = Address(address,name)

            val intent = Intent(applicationContext, SelectLocation_2_5::class.java)
            startActivity(intent) // 후일에 고칠 부분
            addressAdapter.addAddress(addressObject)
        }
        btnStart.setOnClickListener {
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            val intent = Intent(applicationContext, SelectDestination_2_6::class.java)
            startActivity(intent)
        }


    }
    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) result = resources.getDimensionPixelSize(resourceId)
        return result
    }
}