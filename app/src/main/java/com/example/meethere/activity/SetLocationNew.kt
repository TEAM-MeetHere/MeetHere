package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.meethere.R
import com.example.meethere.databinding.ActivitySetLocationNewBinding
import com.example.meethere.fragment.SetLocation1Keyword
import com.example.meethere.fragment.SetLocation2InputList
import kotlinx.android.synthetic.main.activity_set_location_new.*

class SetLocationNew : AppCompatActivity() {
    var flag: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_location_new)

        replaceFragment(SetLocation1Keyword())

        btnPrev.setOnClickListener {
            flag--
            changeFragment(flag)
        }
        btnNext.setOnClickListener {
            flag++
            changeFragment(flag)
        }
    }

    private fun changeFragment(flag_: Int) {
        when (flag_) {
            0 -> {
                finish()
            }
            1 -> {
                replaceFragment(SetLocation1Keyword())
            }
            2 -> {
                replaceFragment(SetLocation2InputList())
            }
            3 -> {
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
                add(R.id.frameLayout, fragment)
            }

            supportFragmentManager.fragments.forEach {
                if (it != fragment && it.isAdded) {
                    hide(it)
                }
            }
        }.commit()
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