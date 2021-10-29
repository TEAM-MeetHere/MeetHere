package com.example.meethere

import java.io.Serializable

data class AddressObject (
    val place_name: String,             // 장소 이름
    var user_name: String,              // 사용자 이름
    val road_address_name: String,      // 도로명주소
    val address_name:String,            // 지번주소
    val lat: Double,                    // 위도
    val lon: Double                     // 경도
) : Serializable