package com.choitaek.meethere.retrofit.request

data class Update(
    var memberId: Long,
    var pw1: String,
    var pw2: String,
    var name: String,
    var phone: String,
    var addressObject: com.choitaek.meethere.retrofit.request.AddressObject
)
