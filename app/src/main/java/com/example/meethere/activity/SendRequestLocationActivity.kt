package com.example.meethere.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.meethere.R
import com.example.meethere.databinding.ActivitySendRequestLocationBinding

class SendRequestLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendRequestLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendRequestLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS), 111
            )
        } else {
//            receiveMsg()
        }

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")

        val tempPhone = phone!!.replace("-", "")

        binding.tvName.setText(name)
        binding.tvPhone.setText(phone)

        val sender = "최규림"
        binding.etRequestMessage.setText("$sender 님의 현재 위치 요청 메시지 입니다.")

        binding.btSend.setOnClickListener {

            val message = binding.etRequestMessage.text.toString()

            if (message != "") {
                val sms = SmsManager.getDefault()
                sms.sendTextMessage(tempPhone, null, message, null, null)
                Toast.makeText(this, "현재 위치 요청 메시지 전송 완료", Toast.LENGTH_LONG).show()

                finish()
            } else {
                Toast.makeText(this, "메시지를 입력해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            receiveMsg()
        }
    }

    private fun receiveMsg() {
        val br = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (sms: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                        binding.tvPhone.setText(sms.originatingAddress)
                        binding.tvName.setText(sms.displayMessageBody)
                    }
                }
            }
        }

        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}