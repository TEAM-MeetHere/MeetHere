package com.example.meethere.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("data")) {
                        register__address.setText(data.getStringExtra("data"))
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth_ID.setOnClickListener {
            var intent = Intent(this, AuthIDActivity::class.java)
            startActivity(intent)
        }

        register__address.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}