package com.example.meethere.retrofit.request

import java.time.LocalDate

data class Bookmark(
    var memberId:Long,
    var destination:String,
    var startAddressList:List<StartAddress>,
    var name:String,
    var date:LocalDate
)
