package com.example.meethere.retrofit

import com.example.meethere.retrofit.request.Login
import com.example.meethere.retrofit.request.Register
import com.example.meethere.retrofit.request.Update
import com.example.meethere.retrofit.request.Verify
import com.example.meethere.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IRetrofit {

    @POST(API.LOGIN)
    fun loginService(@Body login: Login)
            : Call<JsonElement>

    @GET(API.BOOKMARK_LIST)
    fun bookmarkListService(@Query("memberId") memberId: Long)
            : Call<JsonElement>

    @POST(API.REGISTER)
    fun registerService(@Body register: Register)
            : Call<JsonElement>

    @POST(API.VERIFY)
    fun verifyService(@Body verify: Verify)
            : Call<JsonElement>

    @POST(API.UPDATE)
    fun updateService(@Body update: Update)
            : Call<JsonElement>
}