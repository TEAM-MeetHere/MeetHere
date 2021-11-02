package com.example.meethere.retrofit.request

data class Register(
    var email: String,
    var pw: String,
    var repw: String,
    var name: String,
    var phone: String,
    var snsType: String = "WWG",
    var addressObject: com.example.meethere.retrofit.request.AddressObject
)
