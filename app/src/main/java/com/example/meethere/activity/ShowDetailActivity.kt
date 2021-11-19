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
        var wholeRouteList = intent.getParcelableArrayListExtra<ItemComponent>("wholeRoute") as ArrayList<ItemComponent>
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


        if(timewalkfee!!.payment == 0){
            total_walk_time.setText("도보 "+timewalkfee!!.totalWalk+"m")
            total_fee.setText("")
        }
        else {
            total_walk_time.setText("도보 " + timewalkfee!!.totalWalk + "m * ")
            total_fee.setText("" + timewalkfee!!.payment + "원")
        }
        val adapter = DetailRouteAdapter(detailRouteList)
        recycler_view.adapter = adapter

        // 요기까지는 어뎁터를 호출해서 한다고 치고 여기서 다시 api호출이 되는지 봐야함





        other_route.setOnClickListener {
            // 현재 화면은 상세 보기 화면이고 다른 경로를 눌렀을 때 다른 경로에 대한 정보가 나와야함.
            // API 다시 호출.
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            val intent = Intent(this@ShowDetailActivity, OtherRouteActivity::class.java)
            intent.putExtra("detail", detailRouteList)
            intent.putExtra("whole", wholeRouteList)
            intent.putExtra("sourceDestinationInfo", timewalkfee)
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