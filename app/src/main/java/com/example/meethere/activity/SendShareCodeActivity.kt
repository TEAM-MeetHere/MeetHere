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
import com.example.meethere.databinding.ActivitySendShareCodeBinding

class SendShareCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendShareCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendShareCodeBinding.inflate(layoutInflater)
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

        binding.btSend.setOnClickListener {

            val shareCode = binding.etShareCode.text.toString()

            if (shareCode != "") {
                val sender = "최규림"
                val message = "$sender 님의 공유코드 입니다. 공유코드 : $shareCode"
                val sms = SmsManager.getDefault()
                sms.sendTextMessage(tempPhone, null, message, null, null)
                Toast.makeText(this, "공유코드 메시지 전송 완료", Toast.LENGTH_LONG).show()

                finish()
            } else {
                Toast.makeText(this, "공유코드를 입력해주세요.", Toast.LENGTH_LONG).show()
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
            receiveMsg()
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