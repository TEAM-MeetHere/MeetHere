package com.example.meethere.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.meethere.R
import com.example.meethere.databinding.ActivityAddFriendBinding

class AddFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveBtn.setOnClickListener {
            // 저장하는 코드
        }
    }
}