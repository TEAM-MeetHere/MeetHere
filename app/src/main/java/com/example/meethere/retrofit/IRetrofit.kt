package com.example.meethere.retrofit

import com.example.meethere.retrofit.request.*
import com.example.meethere.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {

    //로그인
    @POST(API.LOGIN)
    fun loginService(@Body login: Login)
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
        @Query("phone") phone: String,
    )
            : Call<JsonElement>

    //회원 정보 가져오기
    @GET(API.USER_INFO)
    fun findUserInfoService(@Query("memberId") memberId: Long)
            : Call<JsonElement>

    //회원 탈퇴
    @DELETE(API.DELETE_MEM)
    fun deleteMemberService(@Query("memberId") memberId: Long)
            : Call<JsonElement>

    //즐겨찾기 저장
    @POST(API.SAVE_BOOKMARK)
    fun saveBookmarkService(@Body bookmark: Bookmark)
            : Call<JsonElement>

    //즐겨찾기 목록
    @GET(API.BOOKMARK_LIST)
    fun bookmarkListService(@Query("memberId") memberId: Long)
            : Call<JsonElement>

    //해당 즐겨찾기 출발 리스트
    @GET(API.FIND_START_ADDRESS_LIST)
    fun findStartAddressListService(@Query("bookmarkId") bookmarkId: Long)
            : Call<JsonElement>

    //즐겨찾기 수정
    @POST(API.UPDATE_BOOKMARK)
    fun updateBookmarkService(@Body updateBookmark: UpdateBookmark)
            : Call<JsonElement>

    //즐겨찾기 삭제
    @DELETE(API.DELETE_BOOKMARK)
    fun deleteBookmarkService(@Query("bookmarkId") bookmarkId: Long)
            : Call<JsonElement>

    //공유코드 저장
    @POST(API.SAVE_SHARE)
    fun saveShareService(@Body share: Share)
            : Call<JsonElement>

    //공유코드 도착주소 불러오기
    @GET(API.SHARE_DESTINATION)
    fun shareDestinationService(@Query("code") code: String)
            : Call<JsonElement>

    //공유코드 출발주소 가져오기
    @GET(API.SHARE_START)
    fun shareStartService(@Query("shareId") shareId: Long)
            : Call<JsonElement>

    //친구 찾기
    @GET(API.FIND_FRIEND)
    fun findFriendService(
        @Query("email") email: String,
        @Query("name") name: String,
        @Query("phone") phone: String,
    ): Call<JsonElement>

    //친구 추가
    @POST(API.ADD_FRIEND)
    fun addFriendService(
        @Query("memberId") memberId: Long,
        @Query("friendId") friendId: Long,
    ): Call<JsonElement>

    //친구 목록
    @GET(API.FRIEND_LIST)
    fun friendListService(@Query("memberId") memberId: Long)
            : Call<JsonElement>

    //친구 삭제
    @DELETE(API.DELETE_FRIEND)
    fun deleteFriendService(@Query("friendId") friendId: Long)
            : Call<JsonElement>

}