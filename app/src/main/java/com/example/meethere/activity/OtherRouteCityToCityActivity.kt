package com.example.meethere.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.meethere.R
import com.example.meethere.adapter.ViewPagerAdapterCity
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OtherRouteCityToCityActivity : AppCompatActivity(){
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_route_city_to_city)

        // 전체 경로 요약 정보, 모든 경로의 상세 경로 정보  다 받아옴.
        var wholeDetailRouteList = intent.getParcelableArrayListExtra<RouteItemComponent>("CityToCityDetailToOtherDetailRoute") as ArrayList<ArrayList<RouteItemComponent>>
        var wholeRouteList = intent.getParcelableArrayListExtra<ItemComponent>("CityToCityDetailToOtherWholeRoute") as ArrayList<ItemComponent>

        tabLayout = findViewById(R.id.tab_city)
        viewPager2 = findViewById(R.id.viewpager2_city)

        viewPager2.adapter =ViewPagerAdapterCity(this,wholeDetailRouteList, wholeRouteList)
        Log.d("메인에서 view pager어뎁터 확인", "->확인")
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "시외버스"
                1 -> tab.text = "열차"
                else -> tab.text = "공항"
            }
        }.attach()
        Log.d("메인에서 탭 생성 확인", "->확인")

        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_transport_bus_20dp)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_transport_train_20dp)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_transport_air_20dp)


        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        val toolbar = supportActionBar
        toolbar!!.title="다른 경로"

        toolbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setDisplayHomeAsUpEnabled(true)
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