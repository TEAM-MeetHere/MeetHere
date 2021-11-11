package com.example.meethere.graph

data class RailroadObject(
    val railroad_time: Int,     // 철도 소요시간
    val railroad_line: String,   // 철도 호선
    val railroad_start: StationObject,   // 출발
    val railroad_end: StationObject     // 도착
)