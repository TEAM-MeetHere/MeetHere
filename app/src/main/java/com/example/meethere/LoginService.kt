package com.example.meethere

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {
    @FormUrlEncoded
    @POST("/api/members/login/")
    fun requestLogin(
        @Field("email") email:String,
        @Field("pw") pw:String
    ) : Call<Login>
}