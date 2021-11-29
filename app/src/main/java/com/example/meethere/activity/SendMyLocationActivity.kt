package com.example.meethere.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meethere.databinding.ActivitySendMyLocationBinding
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG

class SendMyLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendMyLocationBinding
    private var myLat: Double = 0.0
    private var myLon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMyLocationBinding.inflate(layoutInflater)
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

        }

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@SendMyLocationActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0)
        } else {
            when {
                isNetworkEnabled -> {
                    val location =
                        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    myLat = location?.latitude!!
                    myLon = location.longitude
                    Log.d(TAG, "myLat = $myLat, myLon = $myLon")
                }
                isGPSEnabled -> {
                    val location =
                        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    myLat = location?.latitude!!
                    myLon = location.longitude
                    Log.d(TAG, "myLat = $myLat, myLon = $myLon")
                }
                else -> {

                }
            }
        }

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")

        val tempPhone = phone!!.replace("-", "")

        binding.tvName.setText(name)
        binding.tvPhone.setText(phone)

        val sender = App.prefs.username

        binding.etResponseMessage.setText("$sender 님의 위치입니다. ")

        binding.btSend.setOnClickListener {

            val address = "$myLat,$myLon"
            val address_name = "http://maps.google.com/maps?f=q&q=$address"
            val message = binding.etResponseMessage.text.toString() + address_name

            if (message != "") {
                val sms = SmsManager.getDefault()
                sms.sendTextMessage(tempPhone, null, message, null, null)
                Toast.makeText(this, "현재 위치 응답 메시지 전송 완료", Toast.LENGTH_LONG).show()
                Log.d(TAG, "phone = $phone")
                Log.d(TAG, "tempPhone = $tempPhone")
                Log.d(TAG, "message = $message")

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