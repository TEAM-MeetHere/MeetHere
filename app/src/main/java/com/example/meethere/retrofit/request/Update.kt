package com.example.meethere.retrofit.request

data class Update(
    var memberId: Long,
    var pw1: String,
    var pw2: String,
    var name: String,
    var address: String,
    var phone: String
)
