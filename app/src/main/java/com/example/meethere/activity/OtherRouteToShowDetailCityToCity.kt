package com.example.meethere.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.R
import com.example.meethere.adapter.DetailRouteAdapter
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent
import kotlinx.android.synthetic.main.activity_show_detail_except_button.*

class OtherRouteToShowDetailCityToCity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail_except_button)

        val intent = intent

        var t = intent.getIntExtra("ClickToEachDetailTimeCityToCity", 0)
        var w = intent.getIntExtra("ClickToEachDetailWalkCityToCity", 0)
        var p = intent.getIntExtra("ClickToEachDetailFeeCityToCity", 0)
        var item_index = intent.getIntExtra("ClickToEachDetailIndexCityToCity", 0)
        var wholeroute = intent.getParcelableArrayListExtra<ItemComponent>("ClickToEachDetailWholeRouteCityToCity") as ArrayList<ItemComponent>
        var wholedetailroute = intent.getParcelableArrayListExtra<RouteItemComponent>("ClickToEachDetailWholeDetailRouteCityToCity") as ArrayList<ArrayList<RouteItemComponent>>

        if(t > 60){
            total_time_except_button.setText(""+(t / 60)+"시간 "+(t % 60)+"분")
        }
        else{
            total_time_except_button.setText(""+t+"분")
        }

        total_walk_time_except_button.setText("도보 "+w+"분")
        total_fee_except_button.setText("")

        val adapter = DetailRouteAdapter(wholedetailroute, wholeroute, item_index)
        recycler_view_except_button.adapter = adapter

        val toolbar = supportActionBar
        toolbar!!.title="상세 경로"

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