package com.example.meethere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_select_destination26.*

class SelectDestination_2_6 : AppCompatActivity() {
    lateinit var tabTime: selectDestination_sort_time
    lateinit var tabDistance: selectDestination_sort_distance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_destination26)
        tabTime = selectDestination_sort_time()
        tabDistance = selectDestination_sort_distance()
        supportFragmentManager.beginTransaction().add(R.id.frameLayout, tabTime).commit()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        //Tab1
                        replaceView(tabTime)
                    }
                    1 -> {
                        //Tab2
                        replaceView(tabDistance)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            private fun replaceView(tab: Fragment) {
                //화면 변경
                var selectedFragment: Fragment? = null
                selectedFragment = tab
                selectedFragment?.let {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, it).commit()
                }
            }
        })
        buttonTemporary.setOnClickListener {
            val intent2 = Intent(applicationContext, ShowResult_2_7::class.java)
            startActivity(intent2)
        }
    }
}