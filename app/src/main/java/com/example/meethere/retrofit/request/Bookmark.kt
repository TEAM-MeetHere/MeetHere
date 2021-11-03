package com.example.meethere.retrofit.request

data class Bookmark(
    var memberId: Long,
    var placeName: String,
    var username: String,
    var dateName: String,
    var roadAddressName: String,
    var addressName: String,
    var lat: Double,
    var lon: Double,
    var date: String,
    var startAddressList: List<StartAddress>
)
