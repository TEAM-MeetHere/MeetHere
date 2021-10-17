package com.example.meethere.retrofit

import com.example.meethere.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IRetrofit {

    @POST(API.LOGIN)
    fun loginService(@Body login:Login):Call<JsonElement>
}