package com.example.meethere.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.meethere.R
import com.example.meethere.adapter.ViewPagerAdapter
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.TimeWalkFee
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.view.MenuItem
import android.widget.Toast
import com.example.meethere.objects.RouteItemComponent
import kotlinx.android.synthetic.main.activity_other_route.*

class OtherRouteActivity : AppCompatActivity() {
//    var detailroutelist : MutableList<RouteItemComponent> = arrayListOf()
//    var routelist : MutableList<ItemComponent> = arrayListOf()
//    var sourceDestinationInfo :TimeWalkFee = mutableListOf<TimeWalkFee>()
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_route)

        tabLayout = findViewById(R.id.transport_tab)
        viewPager2 = findViewById(R.id.viewpager2_layout)

        // 전체 경로 요약 정보, 모든 경로의 상세 경로 정보  다 받아옴.
        var wholeDetailRouteList = intent.getParcelableArrayListExtra<RouteItemComponent>("detailToOtherDetailRoute") as ArrayList<ArrayList<RouteItemComponent>>
        var wholeRouteList = intent.getParcelableArrayListExtra<ItemComponent>("detailToOtherWholeRoute") as ArrayList<ItemComponent>

        viewPager2.adapter = ViewPagerAdapter(this, wholeDetailRouteList, wholeRouteList)
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
            //페이지가 변경될떄마다 호출되는 콜백함수
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        val toolbar = supportActionBar
        toolbar!!.title="다른 경로"

        toolbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setDisplayHomeAsUpEnabled(true)
//        setSupportActionBar(toolbar)
//        supportActionBar?.apply {
//            title = "다른 경로"
//            setDisplayHomeAsUpEnabled(true)
//            setDisplayShowHomeEnabled(true)
//        }
//        toolbar.setTitleTextColor(Color.WHITE)
//        toolbar.navigationIcon?.apply{
//            setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
//        }
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