package com.example.meethere.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.meethere.objects.AddressObject
import com.example.meethere.databinding.ActivityEditBinding
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.retrofit.request.Update
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_edit.*
import org.json.JSONObject
import java.util.regex.Pattern

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding

    private var addressObject = AddressObject(
        App.prefs.place_name.toString(), App.prefs.username.toString(),
        App.prefs.road_address_name.toString(), App.prefs.address_name.toString(),
        App.prefs.lat!!.toDouble(), App.prefs.lon!!.toDouble())

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("addressObject")) {
                        addressObject = data.getSerializableExtra("addressObject") as AddressObject
                        binding.editAddress.setText(addressObject.road_address_name)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editName.setText(App.prefs.username)
        binding.editAddress.setText(App.prefs.road_address_name)
        binding.editPhone.setText(App.prefs.phone)
        binding.editPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        //수정 버튼 클릭시
        binding.editInfoButton.setOnClickListener {
            Log.d(TAG, "EditActivity - 수정 버튼 클릭 / currentSearchType")

            var memberId: Long = App.prefs.memberId!!
            var pw1: String? = binding.editPw.text.toString()
            var pw2: String? = binding.editRewritePw.text.toString()
            var name: String? = binding.editName.text.toString()
            var address: String? = binding.editAddress.text.toString()
            var phone: String? = binding.editPhone.text.toString()

            if (checkValidation(pw1.toString(),
                    pw2.toString(),
                    name.toString(),
                    phone.toString())
            ) return@setOnClickListener

            var myUpdate = Update(
                memberId, pw1!!, pw2!!, name!!, phone!!,
                com.example.meethere.retrofit.request.AddressObject(addressObject.place_name,
                    addressObject.road_address_name,
                    addressObject.address_name,
                    addressObject.lat,
                    addressObject.lon)
            )

            RetrofitManager.instance.updateService(
                update = myUpdate,
                completion = { responseState, responseBody ->
                    when (responseState) {

                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {
                            Log.d(TAG, "API 호출 성공 : $responseBody")
                            Log.d(TAG, "pw1 = $pw1")
                            Log.d(TAG, "pw2 = $pw2")
                            Log.d(TAG, "name = $name")
                            Log.d(TAG, "address = $address")
                            Log.d(TAG, "phone = $phone")
                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if (statusCode == 200) {
                                App.prefs.username = name
                                App.prefs.phone = phone
                                App.prefs.place_name = addressObject.place_name
                                App.prefs.road_address_name = addressObject.road_address_name
                                App.prefs.address_name = addressObject.address_name
                                App.prefs.lat = addressObject.lat.toString()
                                App.prefs.lon = addressObject.lon.toString()

                                var message = jsonObject.getString("message")
                                Log.d(TAG, "message = $message")
                                Toast.makeText(this@EditActivity, message, Toast.LENGTH_SHORT)
                                    .show()

                                finish()
                            } else {
                                var errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(this@EditActivity, errorMessage, Toast.LENGTH_SHORT)
                                    .show()
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

        edit_address.setOnClickListener {
            val intent = Intent(this, SearchAddressActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun checkValidation(
        PW: String,
        RE_PW: String,
        NAME: String,
        PHONE: String,
    ): Boolean {
        //비밀번호 미입력 시
        if (PW == "") {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }
        //비밀번호 유효성 확인
        if (!Pattern.matches("(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{6,12}", PW)) {
            Toast.makeText(this, "비밀번호 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
            return true
        }

        //재입력 비밀번호 미입력 시
        if (RE_PW == "") {
            Toast.makeText(this, "확인 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }
        //재입력 비밀번호 일치 여부 확인
        if (PW != RE_PW) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return true
        }

        //이름 미입력 시
        if (NAME == "") {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }

        //핸드폰 번호 유효성 확인
        if (!Pattern.matches("^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$", PHONE)) {
            Toast.makeText(this, "올바른 휴대폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
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