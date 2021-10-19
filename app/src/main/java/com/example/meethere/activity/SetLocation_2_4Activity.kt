package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.Address
import com.example.meethere.R
import com.example.meethere.adapter.AddressAdapter
import kotlinx.android.synthetic.main.activity_set_location24.*

class SetLocation_2_4Activity : AppCompatActivity() {
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_location24)
        addressAdapter = AddressAdapter(mutableListOf())
        recyclerViewAddress.adapter = addressAdapter
        recyclerViewAddress.layoutManager = LinearLayoutManager(this)

/*        val address1 = "인천광역시 미추홀구 인하로 100"
        val name1 = "김철수"
        val addressObject1 = Address(address1,name1)
        val address2 = "인천광역시 남동구 정각로 29 인천광역시청"
        val name2 = "박민수"
        val addressObject2 = Address(address2,name2)
        val address3 = "인천광역시 서구 가좌동 217"
        val name3 = "최진수"
        val addressObject3 = Address(address3,name3)

        addressAdapter.addAddress(addressObject1)
        addressAdapter.addAddress(addressObject2)
        addressAdapter.addAddress(addressObject3)*/

        var arrayAD = arrayOf<String>(
            "인천광역시 미추홀구 인하로 100",
            "인천광역시 남동구 정각로 29",
            "인천광역시 서구 가좌동 217",
            "인천광역시 미추홀구 주안동 24-24"
        )
        var arrayNM = arrayOf<String>("김철수", "박민수", "최진수", "이상수")
        var arrayOB = Array(20) { Address("임시 주소", "임시 이름") }

        for (a in 0..3) {
            arrayOB[a] = Address(arrayAD[a], arrayNM[a])
        }

        var listnumber = 0
        btnAdd.setOnClickListener {
            /* val address = textViewAddress.text.toString()
             val name = textViewName.text.toString()*/
            val address = "주소 추가"
            val name = "임시"
            val addressObject = Address(address, name)

            val intent = Intent(applicationContext, SelectLocation_2_5Activity::class.java)
            startActivity(intent) // 후일에 고칠 부분
            addressAdapter.addAddress(arrayOB[listnumber])
            listnumber++
        }
        btnStart.setOnClickListener {
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            val intent = Intent(applicationContext, SelectDestination_2_6Activity::class.java)
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