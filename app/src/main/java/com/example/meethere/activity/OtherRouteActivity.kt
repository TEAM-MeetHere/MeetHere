package com.example.meethere.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.meethere.R
import com.example.meethere.adapter.ViewPagerAdapter
import com.example.meethere.objects.ItemComponent
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OtherRouteActivity : AppCompatActivity() {


    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_route)

        tabLayout = findViewById(R.id.transport_tab)
        viewPager2 = findViewById(R.id.viewpager2_layout)

        var routelist = intent.getParcelableArrayListExtra<ItemComponent>("rlist") as ArrayList<ItemComponent>


        viewPager2.adapter = ViewPagerAdapter(this, routelist)
        Log.d("메인에서 view pager어뎁터 확인", "->확인")
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "버스"
                1 -> tab.text = "지하철"
                else -> tab.text = "버스+지하철"
            }
        }.attach()
        Log.d("메인에서 탭 생성 확인", "->확인")

        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_transport_bus_20dp)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_transport_light_train_20dp)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_transport_car_20dp)

        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }
}