package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.meethere.R
import com.example.meethere.adapter.DetailRouteAdapter
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent
import com.example.meethere.objects.TimeWalkFee
import kotlinx.android.synthetic.main.activity_show_detail.*

class ShowDetailActivity : AppCompatActivity() {
    /*
    * se
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)

        val intent = intent
        val Name = intent.getStringExtra("Name")

        var detailRouteList = intent.getParcelableArrayListExtra<RouteItemComponent>("routeDetail") as ArrayList<RouteItemComponent>
        var timewalkfee = intent.getParcelableExtra<TimeWalkFee>("twp")

        Log.d("디테일에서 받아온 시간 정보", timewalkfee!!.totalTime.toString())
        Log.d("디테일에서 받은 거리 정보", timewalkfee!!.totalWalk.toString())
        Log.d("디테일에서 받은 요금 정보", timewalkfee!!.payment.toString())

        if(timewalkfee!!.totalTime > 60){
            total_time.setText(""+(timewalkfee!!.totalTime / 60)+"시간 "+(timewalkfee!!.totalTime % 60)+"분")
        }
        else{
            total_time.setText(""+timewalkfee!!.totalTime+"분")
        }


        total_walk_time.setText("도보 "+timewalkfee!!.totalWalk+"m * ")
        total_fee.setText(""+timewalkfee!!.payment+"원")
        val adapter = DetailRouteAdapter(detailRouteList)
        recycler_view.adapter = adapter

        other_route.setOnClickListener {
            // API 다시 호출.
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            val intent = Intent(applicationContext, ShowDetailMapActivity::class.java)
            startActivity(intent)
        }
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