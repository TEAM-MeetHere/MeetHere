package com.example.meethere

import android.content.Context
import android.content.ContextParams
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth_ID.setOnClickListener({
            var intent = Intent(this, AuthID::class.java)
            startActivity(intent)
        })
    }
}