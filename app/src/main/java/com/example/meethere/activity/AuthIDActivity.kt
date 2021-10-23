package com.example.meethere.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.R
import com.example.meethere.databinding.ActivityAuthIdBinding

class AuthIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthIdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var authCode = binding.authIDCode.text.toString().toInt()

    }
}