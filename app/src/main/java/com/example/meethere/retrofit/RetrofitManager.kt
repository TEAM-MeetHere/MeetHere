package com.example.meethere.retrofit

import android.util.Log
import com.example.meethere.retrofit.request.*
import com.example.meethere.utils.API
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitManager {

    //싱글톤 유지되도록, retrofit 가져올 때마다 해당 인스턴스
    companion object {
        val instance = RetrofitManager()
    }

    //http 콜 만들기
    //retrofit interface 가져오기
    private val iRetrofit: IRetrofit? =
        RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    //친구 삭제
    fun deleteFriendService(
        friendId: Long,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {
        val term = friendId ?: ""

        val call = iRetrofit?.deleteFriendService(
            friendId = term as Long
        ) ?: return

        callEnqueue(call, completion)
    }

    //친구 목록
    fun friendListService(
        memberId: Long,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {

        val term = memberId ?: ""

        val call = iRetrofit?.friendListService(
            memberId = term as Long
        ) ?: return

        callEnqueue(call, completion)
    }

    //친구 추가
    fun addFriendService(
        memberId: Long,
        friendId: Long,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {

        val term1 = memberId ?: ""
        val term2 = friendId ?: ""

        val call = iRetrofit?.addFriendService(
            memberId = term1 as Long,
            friendId = term2 as Long
        ) ?: return

        callEnqueue(call, completion)
    }

    //친구 찾기
    fun findFriendService(
        email: String,
        name: String,
        phone: String,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {

        val term1 = email ?: ""
        val term2 = name ?: ""
        val term3 = phone ?: ""

        val call = iRetrofit?.findFriendService(
            email = term1 as String,
            name = term2 as String,
            phone = term3 as String
        ) ?: return

        callEnqueue(call, completion)
    }

    //공유코드 내용(출발지점 리스트) 불러오기
    fun shareStartService(shareId: Long, completion: (RESPONSE_STATE, String) -> Unit) {
        val term = shareId ?: ""
        val call = iRetrofit?.shareStartService(shareId = term as Long) ?: return

        callEnqueue(call, completion)
    }

    //공유코드 내용(도착지점) 불러오기
    fun shareDestinationService(code: String, completion: (RESPONSE_STATE, String) -> Unit) {
        val term = code ?: ""
        val call = iRetrofit?.shareDestinationService(code = term as String) ?: return

        callEnqueue(call, completion)
    }


    //공유코드 저장
    fun saveShareService(share: Share, completion: (RESPONSE_STATE, String) -> Unit) {
        val term = share ?: ""
        val call = iRetrofit?.saveShareService(share = term as Share) ?: return

        callEnqueue(call, completion)
    }

    //즐겨찾기 저장 API 호출
    fun saveBookmarkService(bookmark: Bookmark, completion: (RESPONSE_STATE, String) -> Unit) {
        val term = bookmark ?: ""
        val call = iRetrofit?.saveBookmarkService(bookmark = term as Bookmark) ?: return

        callEnqueue(call, completion)
    }

    //즐겨찾기 리스트 API 호출
    fun bookmarkListService(memberId: Long?, completion: (RESPONSE_STATE, String) -> Unit) {

        //null이면 "", 아니면 해당 값 설정
        val term = memberId ?: ""
        var call = iRetrofit?.bookmarkListService(memberId = term as Long) ?: return

        callEnqueue(call, completion)
    }

    //해당 즐겨찾기 출발지점 리스트 불러오기 API 호출
    fun findStartAddressListService(
        bookmarkId: Long,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {
        var term = bookmarkId ?: ""
        val call = iRetrofit?.findStartAddressListService(bookmarkId = term as Long) ?: return

        callEnqueue(call, completion)
    }

    //즐겨찾기 수정 API 호출
    fun updateBookmarkService(
        updateBookmark: UpdateBookmark,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {
        val term = updateBookmark ?: ""
        val call =
            iRetrofit?.updateBookmarkService(updateBookmark = term as UpdateBookmark) ?: return

        callEnqueue(call, completion)
    }

    //해당 즐겨찾기 삭제 API 호출
    fun deleteBookmarkService(bookmarkId: Long, completion: (RESPONSE_STATE, String) -> Unit) {
        var term = bookmarkId ?: ""
        val call = iRetrofit?.deleteBookmarkService(bookmarkId = term as Long) ?: return

        callEnqueue(call, completion)
    }

    //회원 탈퇴 API 호출
    fun deleteMemberService(
        memberId: Long,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {
        val term = memberId ?: ""

        val call = iRetrofit?.deleteMemberService(
            memberId = term as Long
        ) ?: return

        callEnqueue(call, completion)
    }

    //해당 회원 정보 가져오기
    fun findUserInfoService(
        memberId: Long,
        completion: (RESPONSE_STATE, String) -> Unit,
    ) {
        val term = memberId?:""
        val call = iRetrofit?.findUserInfoService(
            memberId = term as Long
        ) ?: return

        callEnqueue(call, completion)
    }

    //회원 비밀번호 찾기 API 호출
    fun findPwService(
        email: String,
        name: String,
        phone: String,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {

        val term1 = email ?: ""
        val term2 = name ?: ""
        val term3 = phone ?: ""

        val call = iRetrofit?.findPwService(
            email = term1 as String,
            name = term2 as String,
            phone = term3 as String
        ) ?: return

        callEnqueue(call, completion)
    }

    //회원 아이디 찾기 API 호출
    fun findIdService(name: String, phone: String, completion: (RESPONSE_STATE, String) -> Unit) {

        val term1 = name ?: ""
        val term2 = phone ?: ""
        val call =
            iRetrofit?.findIdService(name = term1 as String, phone = term2 as String) ?: return

        callEnqueue(call, completion)
    }

    //회원정보 수정 API 호출
    fun updateService(update: Update, completion: (RESPONSE_STATE, String) -> Unit) {

        val term = update ?: ""
        var call = iRetrofit?.updateService(update = term as Update) ?: return

        callEnqueue(call, completion)
    }


    //회원가입 인증코드 입력 API 호출
    fun verifyService(verify: Verify, completion: (RESPONSE_STATE, String) -> Unit) {

        val term = verify ?: ""
        var call = iRetrofit?.verifyService(verify = term as Verify) ?: return

        callEnqueue(call, completion)
    }

    //회원가입 API 호출
    fun registerService(register: Register, completion: (RESPONSE_STATE, String) -> Unit) {

        val term = register ?: ""
        var call = iRetrofit?.registerService(register = term as Register) ?: return

        callEnqueue(call, completion)
    }

    //로그인 API 호출
    fun loginService(login: Login?, completion: (RESPONSE_STATE, String) -> Unit) {

        //null이면 "", 아니면 해당 값 설정
        val term = login ?: ""
        val call = iRetrofit?.loginService(login = term as Login) ?: return

        callEnqueue(call, completion)
    }

    private fun callEnqueue(
        call: Call<JsonElement>,
        completion: (RESPONSE_STATE, String) -> Unit
    ) {
        call.enqueue(object : Callback<JsonElement> {
            //응답 성공
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "Retrofitmanager - onResponse() called /t:${response.raw()}")
                completion(RESPONSE_STATE.OKAY, response.body().toString())
            }

            //응답 실패
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATE.FAIL, t.toString())
            }
        })
    }
}