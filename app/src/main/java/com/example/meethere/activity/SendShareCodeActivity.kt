package com.example.meethere.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import com.example.meethere.databinding.ActivitySendShareCodeBinding

class SendShareCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendShareCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendShareCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvName.setText("asd")
        binding.tvPhone.setText("phone asdasd")

        binding.btSend.setOnClickListener {

            val shareCode = binding.etShareCode.text.toString()

            if (shareCode != "") {
                val sender = "최규림"
                val message = "$sender 님의 공유코드 입니다. 공유코드 : $shareCode"
                val sms = SmsManager.getDefault()
                sms.sendTextMessage("", null, message, null, null)
                Toast.makeText(this, "공유코드 메시지 전송 완료", Toast.LENGTH_LONG).show()

                finish()
            } else {
                Toast.makeText(this, "공유코드를 입력해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }
}