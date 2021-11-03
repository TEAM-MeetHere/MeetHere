package com.example.meethere

data class SearchResultItem(
    val name: String,      // 장소명
    val road: String,      // 도로명 주소
    val address: String,   // 지번 주소
    val lat: Double,         // 경도(Longitude)
    val lon: Double          // 위도(Latitude)
)