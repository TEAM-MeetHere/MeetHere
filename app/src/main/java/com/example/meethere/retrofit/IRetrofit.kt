package com.example.meethere.retrofit

import com.example.meethere.retrofit.request.*
import com.example.meethere.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IRetrofit {

    //로그인
    @POST(API.LOGIN)
    fun loginService(@Body login: Login)
            : Call<JsonElement>

    //즐겨찾기 목록
    @GET(API.BOOKMARK_LIST)
    fun bookmarkListService(@Query("memberId") memberId: Long)
            : Call<JsonElement>

    //회원가입
    @POST(API.REGISTER)
    fun registerService(@Body register: Register)
            : Call<JsonElement>

    //메일로 인증번호 발송
    @POST(API.VERIFY)
    fun verifyService(@Body verify: Verify)
            : Call<JsonElement>

    //회원정보 수정
    @POST(API.UPDATE)
    fun updateService(@Body update: Update)
            : Call<JsonElement>

    //아이디 찾기
    @GET(API.FIND_ID)
    fun findIdService(@Query("name") name: String, @Query("phone") phone: String)
            : Call<JsonElement>

    //비밀번호 찾기(갱신)
    @GET(API.FIND_PW)
    fun findPwService(
        @Query("email") email: String,
        @Query("name") name: String,
        @Query("phone") phone: String
    )
            : Call<JsonElement>

    @POST(API.SAVE_BOOKMARK)
    fun saveBookmarkService(@Body bookmark: Bookmark)
            : Call<JsonElement>

    @POST(API.SAVE_SHARE)
    fun saveShareService(@Body share: Share)
            : Call<JsonElement>

    @GET(API.SHARE_DESTINATION)
    fun shareDestinationService(@Query("code") code: String)
            : Call<JsonElement>

    @GET(API.SHARE_START)
    fun shareStartService(@Query("shareId") shareId: Long)
            : Call<JsonElement>
}