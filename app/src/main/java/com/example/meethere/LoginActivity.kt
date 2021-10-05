package com.example.meethere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        join.setOnClickListener({
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })

        find_id.setOnClickListener({
            val intent = Intent(this, FindID::class.java)
            startActivity(intent)
        })

        find_pw.setOnClickListener({
            val intent = Intent(this,FindPW::class.java)
            startActivity(intent)
        })
    }
}