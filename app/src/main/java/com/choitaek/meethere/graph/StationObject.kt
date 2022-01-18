package com.choitaek.meethere.graph

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class StationObject @JsonCreator constructor(
    @JsonProperty("station_id") val station_id: String,     // 역 ID     오디세이 API
    @JsonProperty("station_name") val station_name: String,   // 역 이름
    @JsonProperty("station_lon") val station_lon: Double,    // 역 경도
    @JsonProperty("station_lat") val station_lat: Double,    // 역 위도
    @JsonProperty("station_line") val station_line: String,   // 역 호선
    @JsonProperty("station_city") val station_city: String,   // 역 도시코드
    @JsonProperty("station_transfer") val station_transfer: Boolean,      // 환승 여부 0 : 없음 1: 있음
    @JsonProperty("station_railroad") val station_railroad: List<RailroadObject>?,  // 이어진 역 간선
)