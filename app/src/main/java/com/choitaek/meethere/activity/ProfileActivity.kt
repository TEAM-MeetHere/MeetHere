package com.choitaek.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.choitaek.meethere.databinding.ActivityProfileBinding
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import org.json.JSONObject

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

        binding.profileUnregister.setOnClickListener {
            RetrofitManager.instance.deleteMemberService(
                memberId = App.prefs.memberId!!.toLong(),
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {
                            Log.d(TAG, "API 호출 성공 : $responseBody")

                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if (statusCode == 200) {
                                val message = jsonObject.getString("message")
                                Log.d(TAG, "message = $message")
                                Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_LONG).show()

                                App.prefs.username = null
                                App.prefs.email = null
                                App.prefs.token = null

                                val intent = Intent(this, LoginActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            } else {
                                val errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                        //API 호출 실패시
                        RESPONSE_STATE.FAIL -> {
                            Log.d(TAG, "API 호출 실패 : $responseBody")
                        }

                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.profileEmail.setText(App.prefs.email)
        binding.profileName.setText(App.prefs.username)
        binding.profileAddress.setText(App.prefs.road_address_name)
        binding.profilePhone.setText(App.prefs.phone)
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