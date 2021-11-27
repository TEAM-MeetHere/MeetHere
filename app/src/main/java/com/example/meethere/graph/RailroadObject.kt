package com.example.meethere.graph

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RailroadObject @JsonCreator constructor(
    @JsonProperty("railroad_time") var railroad_time: Int,     // 철도 소요시간
    @JsonProperty("railroad_line") val railroad_line: String,   // 철도 호선
    @JsonProperty("railroad_endID") val railroad_endID: String,     // 도착 ID
)