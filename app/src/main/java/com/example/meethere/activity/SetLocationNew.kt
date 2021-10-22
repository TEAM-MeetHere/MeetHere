package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.meethere.fragment.SetLocation1Keyword
import com.example.meethere.fragment.SetLocation2InputList
import com.example.meethere.fragment.SetLocation3InputAddress
import kotlinx.android.synthetic.main.activity_set_location_new.*


class SetLocationNew : AppCompatActivity() {
    private var flag: Int = 1
    private val f1: Fragment = SetLocation1Keyword()
    private val f2: Fragment = SetLocation2InputList()
    private val f3: Fragment = SetLocation3InputAddress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.meethere.R.layout.activity_set_location_new)


        supportFragmentManager.beginTransaction()
            .add(com.example.meethere.R.id.frameLayoutSetLocation, f1, "TAG1")
        supportFragmentManager.beginTransaction()
            .add(com.example.meethere.R.id.frameLayoutSetLocation, f2, "TAG2")
        supportFragmentManager.beginTransaction()
            .add(com.example.meethere.R.id.frameLayoutSetLocation, f3, "TAG3")

        replaceFragment(f1)

        btnPrev.setOnClickListener {
            flag--
            changeFragment(flag)
        }
        btnNext.setOnClickListener {
            flag++
            changeFragment(flag)
        }

    }

    fun changeFragment(flag_: Int) {
        when (flag_) {
            0 -> {
                finish()
            }
            1 -> {
                replaceFragment(f1)
            }
            2 -> {
                replaceFragment(f2)
            }
            100 -> {
                replaceFragment(f3)
            }
            else -> {
                flag = 2
                val intent = Intent(applicationContext, SelectDestination_2_6Activity::class.java)
                startActivity(intent)
            }
        }
    }

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

    fun addAddress(address: String, name: String) {
        val fragment: SetLocation2InputList =
            supportFragmentManager.findFragmentByTag("TAG2") as SetLocation2InputList
        fragment.addAddress(address, name)
        replaceFragment(f2)
    }

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