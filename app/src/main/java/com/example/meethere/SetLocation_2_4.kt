package com.example.meethere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        btnAdd.setOnClickListener {
            /* val address = textViewAddress.text.toString()
             val name = textViewName.text.toString()*/
            val address = "인하대"
            val name = "임영택"
            val addressObject = Address(address,name)
            addressAdapter.addAddress(addressObject)
            val intent = Intent(applicationContext, SelectLocation_2_5::class.java)
            startActivity(intent) // 후일에 고칠 부분
        }
        btnStart.setOnClickListener {
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            val intent = Intent(applicationContext, SelectDestination_2_6::class.java)
            startActivity(intent)
        }
    }
}