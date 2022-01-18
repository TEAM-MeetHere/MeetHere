package com.choitaek.meethere.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.choitaek.meethere.R
import com.choitaek.meethere.objects.ItemComponent
import com.choitaek.meethere.objects.RouteItemComponent
import kotlinx.android.synthetic.main.activity_show_detail.*

class DetailRouteAdapter (var dataSet: List<List<RouteItemComponent>>, var wholeDataSet : List<ItemComponent>, var min_idx : Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
//        Log.d("뷰타입", dataSet[min_idx]..layoutType.toString())
        val view : View?
        Log.d("뷰타입", viewType.toString())

        return when(viewType) {
            RouteItemComponent.using_subway_in_transfer -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_subway_to_subway_using_transfer_in_subway,parent, false)
                ViewHolder_1(view)
            }

            RouteItemComponent.using_subway_not_in_transfer -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_subway_to_destination_in_subway,parent, false)
                ViewHolder_2(view)
            }

            RouteItemComponent.using_bus -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_bus_station_to_where_using_bus,parent, false)
                ViewHolder_3(view)
            }

            RouteItemComponent.using_footwalk_to_transfer_between_subway -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_subway_to_subway_using_transfer_in_footwalk,parent, false)
                ViewHolder_4(view)
            }

            RouteItemComponent.using_footwalk_from_subwayExit_to_destination -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_subway_exit_to_destination_using_footwalk,parent, false)
                ViewHolder_5(view)
            }

            RouteItemComponent.using_footwalk_from_source_to_bus_or_subway -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_source_to_bus_or_subway_using_footwalk,parent, false)
                ViewHolder_6(view)
            }

            RouteItemComponent.using_footwalk_from_bus_station_to_destination -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_bus_station_to_destination,parent, false)
                ViewHolder_7(view)
            }

            RouteItemComponent.using_footwalk_in_same_bus_station -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_bus_station_to_bus_station_transfer_in_same_station, parent, false)
                ViewHolder_8(view)
            }

            RouteItemComponent.destination -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_to_destination, parent, false)
                ViewHolder_9(view)
            }

            RouteItemComponent.using_bus_from_city_to_city ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_city_to_city_using_bus, parent, false)
                ViewHolder_10(view)
            }

            RouteItemComponent.using_train_from_city_to_city ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_city_to_city_using_train, parent, false)
                ViewHolder_11(view)
            }

            RouteItemComponent.using_air_from_city_to_city ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.activity_trip_city_to_city_using_air, parent, false)
                ViewHolder_12(view)
            }

            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj = dataSet[min_idx].get(position)
        when (obj.layoutType) {
            1 -> {
                (holder as ViewHolder_1).txtTime.setText(""+obj.sectionTime+"분")
                (holder as ViewHolder_1).txtStation.setText(""+obj.busNoORname+" "+obj.startName+" 승차")
                (holder as ViewHolder_1).txtDirection.setText(""+obj.passStopList[1].first+"방면 ("+obj.way+"행)")

                if(obj.door == "null"){
                    (holder as ViewHolder_1).txtQuitTransferDoor.setText("")
                }
                else {
                    (holder as ViewHolder_1).txtQuitTransferDoor.setText("빠른 환승 " + obj.door)
                }
                (holder as ViewHolder_1).txtStationCount.setText(""+obj.stationCount+"개 역 이동")

                if(obj.passStopList.size == 2){
                    (holder as ViewHolder_1).txtItems.setText(obj.passStopList[1].first)
                }
                else{
                    (holder as ViewHolder_1).txtItems.setText(obj.passStopList[1].first+"\n")
                    for(i in 2 until obj.passStopList.size){
                        (holder as ViewHolder_1).txtItems.append(obj.passStopList[i].first+"\n")
                    }
                }
            }

            2 -> {
                Log.d("2번 뷰", "이거 됨?")
                (holder as ViewHolder_2).txtTime.setText(""+obj.sectionTime+"분")
                (holder as ViewHolder_2).txtStation.setText(""+obj.busNoORname+" "+obj.startName+" 승차")
                (holder as ViewHolder_2).txtDirection.setText(""+obj.passStopList[1].first+"방면 ("+obj.way+"행)")
                (holder as ViewHolder_2).txtStationCount.setText(""+obj.stationCount+"개 역 이동")
                if(obj.passStopList.size == 2){
                    (holder as ViewHolder_2).txtItems.setText(obj.passStopList[1].first)
                }
                else{
                    (holder as ViewHolder_2).txtItems.setText(obj.passStopList[1].first+"\n")
                    for(i in 2 until obj.passStopList.size){
                        (holder as ViewHolder_2).txtItems.append(obj.passStopList[i].first+"\n")
                    }
                }
            }

            3 -> {
                (holder as ViewHolder_3).txtTime.setText(""+obj.sectionTime+"분")
                (holder as ViewHolder_3).txtStation.setText(""+obj.startName+" 승차")
                (holder as ViewHolder_3).txtbusNo.setText(""+obj.busNoORname+"번")
                (holder as ViewHolder_3).txtStationNo.setText(""+obj.stationCount+"개 정류장 이동")
                if(obj.passStopList.size == 2){
                    (holder as ViewHolder_3).txtItems.setText(obj.passStopList[1].first)
                }
                else{
                    (holder as ViewHolder_3).txtItems.setText(obj.passStopList[1].first+"\n")
                    for(i in 2 until obj.passStopList.size){
                        (holder as ViewHolder_3).txtItems.append(obj.passStopList[i].first+"\n")
                    }
                }
            }

            4 ->{
                (holder as ViewHolder_4).txtLastStation.setText(""+obj.endName+" 하차")
            }

            5->{
                if(obj.resultIndex == -1){
                    (holder as ViewHolder_5).txtTime.setText("" + obj.sectionTime + "분")
                    (holder as ViewHolder_5).txtStation.setText("" + obj.endName + " 하차")
                    if(obj.startExitnoORendExitno == "-1"){
                        // odsay api에서 해당 정보 못받아옴.
                        (holder as ViewHolder_5).txtMeter.setText("다음 지점인 " + obj.startName+"까지 도보" +obj.distance + "m")
                    }
                    else {
                        (holder as ViewHolder_5).txtMeter.setText("" + obj.startExitnoORendExitno + "번 출구에서 " + obj.startName + "까지 도보" + obj.distance + "m")
                    }
                }
                else {
                    (holder as ViewHolder_5).txtTime.setText("" + obj.sectionTime + "분")
                    (holder as ViewHolder_5).txtStation.setText("" + obj.endName + " 하차")
                    if(obj.startExitnoORendExitno == "-1"){
                        (holder as ViewHolder_5).txtMeter.setText("" + obj.endName + "에서 " + obj.distance + "m")
                    }
                    else {
                        (holder as ViewHolder_5).txtMeter.setText("" + obj.startExitnoORendExitno + "번 출구에서 " + obj.distance + "m")
                    }
                }
            }

            6->{
                if(obj.resultIndex == -1){
                    // result index가 -1이라는건 진짜 출발지에서 시외 수단 까지 가기 위한 첫걸음
                    (holder as ViewHolder_6).txtTime.setText("" + obj.sectionTime + "분") // 시간은 필요없음.
                    (holder as ViewHolder_6).txtMeter.setText(""+obj.startName+"까지 도보 "+obj.distance+"m")
                }
                else if(obj.resultIndex == -2){
                     // result index가 -2라는건 출발지에서 출발 시외까지 한걸음에 바로 쌉 가능임
                    (holder as ViewHolder_6).txtTime.setText("")
                    (holder as ViewHolder_6).txtMeter.setText(""+obj.endName+"까지 도보 이용")
                }
                else {
                    (holder as ViewHolder_6).txtTime.setText("" + obj.sectionTime + "분")
                    if (obj.startExitnoORendExitno == "NoData") {
                        (holder as ViewHolder_6).txtMeter.setText("도보 " + obj.distance + "m")
                    } else {
                        (holder as ViewHolder_6).txtMeter.setText(obj.startName + "역의 " + obj.startExitnoORendExitno + "번 출구까지 " + obj.distance + "m")
                    }
                }
            }

            7 ->{
                if(obj.resultIndex == -1){
                    (holder as ViewHolder_7).txtTime.setText("" + obj.sectionTime + "분")
                    (holder as ViewHolder_7).txtStation.setText("" +obj.endName+"에 도착해서")
                    if (obj.startExitnoORendExitno == "NoData") {
                        Log.d("view holder7에서의 startName -> ", obj.startName)
                        if(obj.startName == "NoData"){
                            // 목적지케이스
                            (holder as ViewHolder_7).txtMeter.setText("목적지까지 도보 " + obj.distance + "m")
                        }
                        else {
                            (holder as ViewHolder_7).txtMeter.setText("" + obj.startName + " 정류장까지 도보 " + obj.distance + "m")
                        }
                    } else {
                        if(obj.startExitnoORendExitno == "-1"){
                            (holder as ViewHolder_7).txtMeter.setText(obj.startName + "역까지 " + obj.distance + "m")
                        }
                        else {
                            (holder as ViewHolder_7).txtMeter.setText(obj.startName + "역의 " + obj.startExitnoORendExitno + "번 출구까지 " + obj.distance + "m")
                        }
                    }

                }
                else if(obj.resultIndex == -2){
                    // result index가 -1이라는건 시외경로, 거기에 layout Type이 7번이면 목적 시외에서 목적지까지 도보 이용
                    (holder as ViewHolder_7).txtTime.setText("")
                    (holder as ViewHolder_7).txtStation.setText(""+obj.endName+"에서")
                    (holder as ViewHolder_7).txtMeter.setText("목적지까지 도보 이용")
                }
                else {
                    (holder as ViewHolder_7).txtTime.setText("" + obj.sectionTime + "분")
                    (holder as ViewHolder_7).txtStation.setText("" + obj.endName + " 하차")
                    if (obj.startExitnoORendExitno == "NoData") {
                        (holder as ViewHolder_7).txtMeter.setText("도보 " + obj.distance + "m")
                    } else {
                        if(obj.startExitnoORendExitno == "-1"){
                            (holder as ViewHolder_7).txtMeter.setText(obj.startName + "역까지 " + obj.distance + "m")
                        }
                        (holder as ViewHolder_7).txtMeter.setText(obj.startName + "역의 " + obj.startExitnoORendExitno + "번 출구까지 " + obj.distance + "m")
                    }
                }
            }

            8 -> {
                (holder as ViewHolder_8).txtStation.setText(""+obj.endName+"하차")
            }

            9 -> {}

            10 -> {
                (holder as ViewHolder_10).txtTime.setText(""+obj.sectionTime + "분")
                (holder as ViewHolder_10).txtStartStation.setText(""+obj.startName + " 출발")
                (holder as ViewHolder_10).txtEndStation.setText(""+obj.endName+" 도착")
            }

            11 ->{
                (holder as ViewHolder_11).txtStartStation.setText(""+obj.startName+ "출발")
                (holder as ViewHolder_11).txtEndStation.setText(""+obj.endName+ "도착")

                for(i in 0 until obj.passStopList.size){
                    (holder as ViewHolder_11).txtTrainList.append(""+obj.passStopList[i].first+"  |  "+obj.passStopList[i].second+"분 소요\n")
                }
            }

            12 -> {
                (holder as ViewHolder_12).txtTime.setText(""+obj.sectionTime+"분")
                (holder as ViewHolder_12).txtStartStation.setText(""+obj.startName+" 출발")
                (holder as ViewHolder_12).txtEndStation.setText(""+obj.endName+" 도착")
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet[min_idx].size
    }

    override fun getItemViewType(position: Int): Int {
        return dataSet[min_idx].get(position).layoutType
    }

    inner class ViewHolder_1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_subway_time)
        val txtStation : TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_subway_first_station)
        val txtDirection : TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_subway_direction)
        val txtQuitTransferDoor : TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_subway_quit_transfer)
        val txtStationCount : TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_subway_station_count)
        val txtItems : TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_subway_items)
    }

    inner class ViewHolder_2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.subway_to_destination_in_subway_time)
        val txtStation: TextView = itemView.findViewById(R.id.subway_to_destination_in_subway_start_station)
        val txtDirection: TextView = itemView.findViewById(R.id.subway_to_destination_in_subway_direction)
        val txtStationCount : TextView = itemView.findViewById(R.id.subway_to_destination_in_subway_station_count)
        val txtItems: TextView = itemView.findViewById(R.id.subway_to_destination_in_subway_items)
    }

    inner class ViewHolder_3(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.bus_station_to_where_using_bus_time)
        val txtStation: TextView = itemView.findViewById(R.id.bus_station_to_where_using_bus_start_station)
        val txtbusNo: TextView = itemView.findViewById(R.id.bus_station_to_where_using_bus_bus_number)
        val txtStationNo: TextView = itemView.findViewById(R.id.bus_station_to_where_using_bus_move_station_number)
        val txtItems: TextView = itemView.findViewById(R.id.bus_station_to_where_using_bus_move_station_items)
    }

    inner class ViewHolder_4(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLastStation: TextView = itemView.findViewById(R.id.subway_to_subway_using_transfer_in_footwalk_last_station)
    }

    inner class ViewHolder_5(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.subway_exit_to_destination_using_footwalk_time)
        val txtStation: TextView = itemView.findViewById(R.id.subway_exit_to_destination_using_footwalk_last_station)
        val txtMeter: TextView = itemView.findViewById(R.id.subway_exit_to_destination_using_footwalk_meter)
    }

    inner class ViewHolder_6(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.source_to_bus_or_subway_using_footwalk_time)
        val txtMeter: TextView = itemView.findViewById(R.id.source_to_bus_or_subway_using_footwalk_meter)
    }

    inner class ViewHolder_7(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.bus_station_to_destination_time)
        val txtStation: TextView = itemView.findViewById(R.id.bus_station_to_destination_last_station)
        val txtMeter: TextView = itemView.findViewById(R.id.bus_station_to_destination_meter)
    }

    inner class ViewHolder_8(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtStation: TextView = itemView.findViewById(R.id.bus_station_to_bus_station_transfer_in_same_station_last_station)
    }
    inner class ViewHolder_9(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    inner class ViewHolder_10(itemView : View) : RecyclerView.ViewHolder(itemView){
        //버스
        val txtTime: TextView = itemView.findViewById(R.id.city_to_city_using_bus_time)
        val txtStartStation: TextView = itemView.findViewById(R.id.city_to_city_using_bus_start_station)
        val txtEndStation: TextView = itemView.findViewById(R.id.city_to_city_using_bus_end_station)
    }

    inner class ViewHolder_11(itemView : View) : RecyclerView.ViewHolder(itemView){
        //열차
        val txtTrainList: TextView = itemView.findViewById(R.id.city_to_city_using_train_name)
        val txtStartStation: TextView = itemView.findViewById(R.id.city_to_city_using_train_start_station)
        val txtEndStation: TextView = itemView.findViewById(R.id.city_to_city_using_train_end_station)
    }

    inner class ViewHolder_12(itemView : View) : RecyclerView.ViewHolder(itemView){
        //항공
        val txtTime: TextView = itemView.findViewById(R.id.city_to_city_using_air_time)
        val txtStartStation: TextView = itemView.findViewById(R.id.city_to_city_using_air_start_station)
        val txtEndStation: TextView = itemView.findViewById(R.id.city_to_city_using_air_end_station)
    }
}