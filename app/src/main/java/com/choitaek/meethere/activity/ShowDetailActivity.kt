package com.choitaek.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.choitaek.meethere.R
import com.choitaek.meethere.adapter.DetailRouteAdapter
import com.choitaek.meethere.objects.ItemComponent
import com.choitaek.meethere.objects.RouteItemComponent
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

        var wholeDetailRouteList = intent.getParcelableArrayListExtra<RouteItemComponent>("resultToDetailDetailRoute") as ArrayList<ArrayList<RouteItemComponent>>
        var wholeRouteList = intent.getParcelableArrayListExtra<ItemComponent>("resultToDetailWholeRoute") as ArrayList<ItemComponent>
        var min_index = intent.getIntExtra("resultToDetailMinIndex", 0)


//        Log.d("상세 정보 확인", wholeDetailRouteList!![0].toString())
//        Log.d("상세 정보 확인", wholeDetailRouteList[1].toString())
//        Log.d("상세 정보 확인", wholeDetailRouteList[2].toString())
//        Log.d("상세 정보 확인", wholeDetailRouteList[3].toString())
//
//        Log.d("최소값가지는 인덱스", min_index.toString())
//
//        Log.d("최소 시간", wholeRouteList[min_index].totalTime.toString())
//        Log.d("최소 거리", wholeRouteList[min_index].walkTime.toString())
//        Log.d("최소 시간", wholeRouteList[min_index].totalFee.toString())

        if(wholeRouteList!![min_index].totalTime > 60){
            total_time.setText(""+(wholeRouteList!![min_index].totalTime / 60)+"시간 "+(wholeRouteList!![min_index].totalTime % 60)+"분")
        }
        else{
            total_time.setText(""+wholeRouteList!![min_index].totalTime+"분")
        }


        if(wholeRouteList!![min_index].totalFee == 0){
            total_walk_time.setText("도보 "+wholeRouteList!![min_index].walkTime+"분")
            total_fee.setText("")
        }
        else {
            total_walk_time.setText("도보 " + wholeRouteList!![min_index].walkTime + "분 * ")
            total_fee.setText("" + wholeRouteList!![min_index].totalFee + "원")
        }
        val adapter = DetailRouteAdapter(wholeDetailRouteList, wholeRouteList, min_index)
        recycler_view.adapter = adapter


        val toolbar = supportActionBar
        toolbar!!.title="상세보기"

        toolbar.setDisplayHomeAsUpEnabled(true)
        toolbar.setDisplayHomeAsUpEnabled(true)


        other_route.setOnClickListener {
            // API 다시 호출.
            //val intent = Intent(applicationContext, selectDestination_2_6::class.java)
            // 여기서는 이제 최소 시간을 줄 필요는 없음 이제부터는
            val intent = Intent(this@ShowDetailActivity, OtherRouteActivity::class.java)
            intent.putExtra("detailToOtherWholeRoute", wholeRouteList)
            intent.putExtra("detailToOtherDetailRoute", wholeDetailRouteList)
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