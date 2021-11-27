package com.example.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RouteItemComponent (
    val resultIndex : Int,
    val layoutType : Int,
    val trafficType:Int, val distance:Int, val sectionTime:Int,
    val stationCount:Int, val busNoORname : String, val startName : String, val endName:String,
    val passStopList : MutableList<Pair<String, String>>,
    val door : String, val startExitnoORendExitno : String, val way : String) : Parcelable{

    companion object{
        // odsay api상 지하철 1번
        const val using_subway_in_transfer = 1   // trip_subway_to_subway_using_transfer_in_subway
        const val using_subway_not_in_transfer = 2             // trip_subway_to_destination_in_subway
        const val using_bus = 3                       // trip_bus_station_to_where_using_bus
        const val using_footwalk_to_transfer_between_subway = 4 // trip_subway_to_subway_using_transfer_in_footwalk
        const val using_footwalk_from_subwayExit_to_destination = 5 // trip_subway_exit_to_destination_using_footwalk
        const val using_footwalk_from_source_to_bus_or_subway = 6    // trip_source_to_bus_or_subway_using_footwalk
        const val using_footwalk_from_bus_station_to_destination = 7  // trip_bus_station_to_destination
        const val using_footwalk_in_same_bus_station = 8    // trip_bus_station_to_bus_station_transfer_in_same_station
        const val destination = 9
        const val using_bus_from_city_to_city = 10  // 고속 버스 or 시외버스 탑승
        const val using_train_from_city_to_city = 11 // 열차 탑승(모든 종류의 열차(무궁화호 , 새마을호 itx, 등등 다 포함 한 레이아웃에 들어감)
        const val using_air_from_city_to_city = 12  // 비행기 탑승.
    }
}