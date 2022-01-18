package com.choitaek.meethere.retrofit

import android.util.Log
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.isJsonArray
import com.choitaek.meethere.utils.isJsonObejct
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//싱글턴(메모리 하나만 사용)
object RetrofitClient {

    //retrofit client 선언
    private var retrofitClient: Retrofit? = null

    //retrofit client 가져오기
    fun getClient(baseUrl: String): Retrofit? {
        Log.d(TAG, "RetrofitClient - getClient() called")

        //okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                if (original.url.encodedPath.equals("/api/members", true)
                    || original.url.encodedPath.equals("/authenticate", true)
                    || original.url.encodedPath.equals("/api/members/verify", true)
                ) {
                    chain.proceed(original)
                } else {
                    chain.proceed(original.newBuilder().apply {
                        addHeader("Authorization", "Bearer " + App.prefs.token!!)
                    }.build())
                }
            }

        //LOG를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d(TAG, "RetrofitClient - log() called")

                when {
                    //message가 Json 객체 형식일 경우
                    message.isJsonObejct() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    //message가 Json 배열 형식일 경우
                    message.isJsonArray() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    //message가 Json 형식이 아닐 경우
                    else -> {
                        try {
                            Log.d(TAG, JSONObject(message).toString(4))
                        } catch (e: Exception) {
                            Log.d(TAG, message)
                        }
                    }
                }
            }

        })

        //로그에 어느 정도 데이터까지 찍어줄지 (HEADER or BODY or ...)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        //위에서 설정한 호깅 인터셉터를 okhttp 클라이언트에 추가
        client.addInterceptor(loggingInterceptor)

        //connection timeout
        client.connectTimeout(10, TimeUnit.SECONDS) //10초동안 반응 없으면 종료
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true) //실패했을 때, 다시 시도

        //retrofit이 없다면
        if (retrofitClient == null) {
            //retrofit builder를 통해 instance 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())

                //위에서 설정한 클라이언트로 retrofit client를 설정
                .client(client.build())
                .build()
        }
        return retrofitClient
    }
}