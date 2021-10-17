package com.example.meethere.retrofit

import android.util.Log
import com.example.meethere.utils.API
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import com.google.gson.JsonElement
import retrofit2.Call
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


    //로그인 API 호출
    fun loginService(login: Login?, completion: (RESPONSE_STATE, String) -> Unit) {

        //null이면 "", 아니면 해당 값 설정
        val term = login?:""
        val call = iRetrofit?.loginService(login = term as Login)?:return

        call.enqueue(object:retrofit2.Callback<JsonElement>{

            //응답 성공
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / t: ${response.raw()}")
                completion(RESPONSE_STATE.OKAY, response.body().toString())
            }

            //응답 실패
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATE.FAIL, t.toString())
            }
        })
    }
}