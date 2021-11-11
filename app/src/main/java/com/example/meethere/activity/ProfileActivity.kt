package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.meethere.databinding.ActivityProfileBinding
import com.example.meethere.sharedpreferences.App

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editInfoButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

        binding.profileLogout.setOnClickListener {
            App.prefs.username = null
            App.prefs.email = null
            App.prefs.token = null
            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.profileEmail.setText(App.prefs.email)
        binding.profileName.setText(App.prefs.username)
        binding.profileAddress.setText(App.prefs.road_address_name)
        binding.profilePhone.setText(App.prefs.phone)
    }
}