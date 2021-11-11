package com.example.meethere.graph

data class StationObject(
    val station_id: String,     // 역 ID     오디세이 API
    val station_name: String,   // 역 이름
    val station_lon: Double,    // 역 경도
    val station_lat: Double,    // 역 위도
    val station_line: List<String>,   // 역 호선
    val station_transfer: Boolean,      // 환승 여부 0 : 없음 1: 있음
    val station_railroad: List<RailroadObject>  // 이어진 역 간선
)