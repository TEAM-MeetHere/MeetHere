package com.example.meethere.retrofit

data class Register(
    var email: String,
    var pw: String,
    var name: String,
    var address: String,
    var phone: String,
    var snsType: String = "WWG"
)
