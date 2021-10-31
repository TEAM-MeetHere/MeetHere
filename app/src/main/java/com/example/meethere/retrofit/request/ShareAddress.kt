package com.example.meethere.retrofit.request

data class ShareAddress(
    var placeName: String,
    var username: String,
    var roadAddressName: String,
    var addressName: String,
    var lat: Double,
    var lon: Double
)
