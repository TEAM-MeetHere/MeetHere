package com.example.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class RouteCityToCity(val busORtrainORair : Int, val startSTN : String, val endSTN : String, val SX : Double, val SY : Double, val EX : Double, val EY: Double, val LaneInfo : MutableList<Pair<String, Int>>) : Parcelable {
    // 출발역 or 출발 정류장 or 출발 공항  | 목적지 역 or 목적지 정류장 or 목적지 공항 | 출발역 or정류장or공항 경도 | 위도 | 목적지역 or목적지 정류장 or 목적지 공항 경도 | 위도 | 배열(string, int의 pair)
    // string에는 새마을 무궁화호 따위의 것들, 버스의 경우는 그냥 버스니까 이름없으니 no data로 초기화 , 공항은 마찬가지로 이름 없이 비행기이므로 no data, time은 알 수 있음.

    // 이 배열이랑 처음 출발지 목적지 위도 경도를 이용해서 시외만의 itemcomponent와 route item component를 만들면 됨.

    // 열차는 4, 버스는 5, 항공은 6
}