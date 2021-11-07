package com.example.meethere.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import com.example.meethere.R
import com.example.meethere.databinding.ActivitySendRequestLocationBinding

class SendRequestLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendRequestLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendRequestLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvName.setText("asd")
        binding.tvPhone.setText("phone asd")

        val sender = "최규림"
        binding.etRequestMessage.setText("$sender 님의 현재 위치 요청 메시지 입니다.")

        binding.btSend.setOnClickListener {

            val message = binding.etRequestMessage.text.toString()

            if (message != "") {
                val sms = SmsManager.getDefault()
                sms.sendTextMessage("", null, message, null, null)
                Toast.makeText(this, "현재 위치 요청 메시지 전송 완료", Toast.LENGTH_LONG).show()

                finish()
            } else {
                Toast.makeText(this, "메시지를 입력해주세요.", Toast.LENGTH_LONG).show()
            }

        }
    }
}