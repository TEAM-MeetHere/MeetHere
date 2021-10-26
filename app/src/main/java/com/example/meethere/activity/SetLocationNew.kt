package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.meethere.Address
import com.example.meethere.fragment.SetLocation1Keyword
import com.example.meethere.fragment.SetLocation2InputList
import com.example.meethere.fragment.SetLocation3InputAddress
import kotlinx.android.synthetic.main.activity_set_location_new.*


class SetLocationNew : AppCompatActivity() {
    private var flag: Int = 1

    // 프래그먼트 변수 선언
    private val f1: Fragment = SetLocation1Keyword()        // 키워드 입력받는 프래그먼트
    private val f2: Fragment = SetLocation2InputList()      // 입력받은 주소들을 보여주는 프래그먼트
    private val f3: Fragment = SetLocation3InputAddress()   // 새로 주소를 입력하는 프래그먼트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.meethere.R.layout.activity_set_location_new)

        supportFragmentManager.beginTransaction()
            .add(com.example.meethere.R.id.frameLayoutSetLocation, f1, "TAG1")
        supportFragmentManager.beginTransaction()
            .add(com.example.meethere.R.id.frameLayoutSetLocation, f2, "TAG2")
        supportFragmentManager.beginTransaction()
            .add(com.example.meethere.R.id.frameLayoutSetLocation, f3, "TAG3")
        // 세 프래그먼트를 미리 추가하고 f1만 보이도록 설정
        replaceFragment(f1)

        btnPrev.setOnClickListener {
            flag--
            changeFragment(flag)
        }
        btnNext.setOnClickListener {
            if (flag == 2) {
                /*val intent = Intent(applicationContext, SelectDestination_2_6Activity::class.java)*/
                val intent =
                    Intent(applicationContext, SelectDestination_2_6Activity::class.java)
                val fragment2: SetLocation2InputList =
                    supportFragmentManager.findFragmentByTag("TAG2") as SetLocation2InputList

                if (fragment2.getSize() == 0) {
                    var t1 = Toast.makeText(this, "인원 추가하기 버튼으로 인원을 추가해주세요", Toast.LENGTH_SHORT)
                    t1.show()
                    return@setOnClickListener
                }

                val addresses: Array<Address> = fragment2.getData().toTypedArray()
                intent.putExtra("addressData", addresses)
                startActivity(intent)

            } else {
                flag++
                changeFragment(flag)
            }
        }

    }

    // 플래그로 프래그먼트를 관리
    // 0 : 이전(메인) 화면으로
    // 1 : 키워드 입력받는 프래그먼트
    // 2 : 입력받은 주소들을 보여주는 프래그먼트
    // 3 : 주소를 새로 입력하는 프래그먼트
    fun changeFragment(flag_: Int) {
        when (flag_) {
            0 -> {
                finish()
            }
            1 -> {
                flag = 1
                btnNext.visibility = View.VISIBLE
                replaceFragment(f1)
            }
            2 -> {
                flag = 2
                btnNext.visibility = View.VISIBLE
                replaceFragment(f2)
            }
            3 -> {
                flag = 3
                btnNext.visibility = View.GONE
                replaceFragment(f3)
            }
            else -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // 프래그먼트를 인자로 받아 해당 프래그먼트만 show 상태로, 나머지는 hide하여 숨김
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(com.example.meethere.R.id.frameLayoutSetLocation, fragment)
            }

            supportFragmentManager.fragments.forEach {
                if (it != fragment && it.isAdded) {
                    hide(it)
                }
            }
        }.commit()
    }

    // 3번 프래그먼트가 호출할 메인의 함수 : 주소를 추가하는 함수
    fun addAddress(address: String, name: String) {
        // 2번 프래그먼트의 함수를 수행
        val fragment2: SetLocation2InputList =
            supportFragmentManager.findFragmentByTag("TAG2") as SetLocation2InputList
        fragment2.addAddress(address, name)
        changeFragment(2)
    }

    // 하드웨어의 뒤로가기 버튼이 눌렸을 경우
    override fun onBackPressed() {
        flag--
        changeFragment(flag)
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