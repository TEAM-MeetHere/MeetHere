package com.example.meethere.retrofit.request

import com.example.meethere.AddressObject
import java.time.LocalDate

data class Bookmark(
    var memberId:Long,
    var placeName:String,
    var startAddressList:List<StartAddress>,
    var username:String,
    var dateName:String,
    var roadAddressName:String,
    var addressName:String,
    var lat:Double,
    var lon:Double,
    var date:String
)
