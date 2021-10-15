package com.example.meethere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var retrofit = Retrofit.Builder()
            .baseUrl("http://13.124.215.113:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var loginService = retrofit.create(LoginService::class.java)




        join.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        find_id.setOnClickListener {
            val intent = Intent(this, FindID::class.java)
            startActivity(intent)
        }

        find_pw.setOnClickListener {
            val intent = Intent(this, FindPW::class.java)
            startActivity(intent)
        }

        login_btn.setOnClickListener {

            val IDTemporary:String = "adcdm77@naver.com"
            val PWTemporary:String = "asd123!@#"

            loginService.requestLogin(IDTemporary, PWTemporary)
                .enqueue(object : Callback<Login> {
                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
                        Log.d("email", "입력된 아이디 : " + IDTemporary)
                        Log.d("pw", "입력된 비밀번호 : " + PWTemporary)
                        var login = response.body()
                        val JSONSTRINGRESOONSE = response.body().toString()
                        var dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("성공!")
                        /*dialog.setMessage("코드 : " + login?.statusCode.toString() + "\n메시지 : " + login?.message)*/
                        dialog.setMessage(response.toString())
                        dialog.show()
                    }

                    override fun onFailure(call: Call<Login>, t: Throwable) {
                        var dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("실패!")
                        dialog.setMessage("통신에 실패하였습니다")
                        dialog.show()
                    }
                })


/*
            val jsonObject = JSONObject()
            jsonObject.put("email", login_id.text)
            jsonObject.put("pw", login_pw.text)
            val email = jsonObject.getString("email")
            val pw = jsonObject.getString("pw")

            Log.d("email", "입력된 아이디 : " + email)
            Log.d("pw", "입력된 비밀번호 : " + pw)
*/


        }


    }
}