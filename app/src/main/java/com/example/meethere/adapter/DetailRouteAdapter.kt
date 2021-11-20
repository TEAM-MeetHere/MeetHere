package com.example.meethere.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.R
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent
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
                (holder as ViewHolder_1).txtQuitTransferDoor.setText("빠른 환승 "+obj.door)
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
                (holder as ViewHolder_5).txtTime.setText(""+obj.sectionTime+"분")
                (holder as ViewHolder_5).txtStation.setText(""+obj.endName+" 하차")
                (holder as ViewHolder_5).txtMeter.setText(""+obj.startExitnoORendExitno+"번 출구에서 "+obj.distance+"m")
            }

            6->{
                (holder as ViewHolder_6).txtTime.setText(""+obj.sectionTime+"분")
                if(obj.startExitnoORendExitno=="NoData") {
                    (holder as ViewHolder_6).txtMeter.setText("도보 " + obj.distance + "m")
                }
                else{
                    (holder as ViewHolder_6).txtMeter.setText(obj.startName+"역의 "+obj.startExitnoORendExitno+"번 출구까지 "+obj.distance+"m")
                }
            }

            7 ->{
                (holder as ViewHolder_7).txtTime.setText(""+obj.sectionTime+"분")
                (holder as ViewHolder_7).txtStation.setText(""+obj.endName+" 하차")
                if(obj.startExitnoORendExitno=="NoData") {
                    (holder as ViewHolder_7).txtMeter.setText("도보 " + obj.distance + "m")
                }
                else{
                    (holder as ViewHolder_7).txtMeter.setText(obj.startName+"역의 "+obj.startExitnoORendExitno+"번 출구까지 "+obj.distance+"m")
                }
            }

            8 -> {
                (holder as ViewHolder_8).txtStation.setText(""+obj.endName+"하차")
            }

            9 -> {}
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
}