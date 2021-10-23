package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.meethere.*
import com.example.meethere.databinding.ActivityMainBinding
import com.example.meethere.sharedpreferences.App
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setText("안녕하세요 " + App.prefs.username + "님")

        binding.newBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_new, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()
            val okButton = mDialogView.findViewById<Button>(R.id.activity_new_btn)
            okButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        binding.codeBtn.setOnClickListener {
            val mCodeView = LayoutInflater.from(this).inflate(R.layout.activity_input_code, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mCodeView)

            val mAlertDialog = mBuilder.show()
            val okButton = mCodeView.findViewById<Button>(R.id.input_code_btn)
            okButton.setOnClickListener {
                val intent = Intent(this, ShowResult_2_7Activity::class.java)
                startActivity(intent)
                mAlertDialog.dismiss()
            }
        }
        edit_btn.setOnClickListener({
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        })

        bookmark_btn.setOnClickListener({
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        })

        search_btn.setOnClickListener({
            val intent = Intent(this, SetLocation_2_4Activity::class.java)
            startActivity(intent)
        })

    }
}
