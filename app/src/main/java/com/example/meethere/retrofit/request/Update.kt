package com.example.meethere.retrofit.request

import com.example.meethere.AddressObject

data class Update(
    var memberId: Long,
    var pw1: String,
    var pw2: String,
    var name: String,
    var phone: String,
    var place_name: String,             // 장소 이름
    var road_address_name: String,      // 도로명주소
    var address_name:String,            // 지번주소
    var lat: Double,                    // 위도
    var lon: Double                     // 경도
)
