package com.choitaek.meethere.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.choitaek.meethere.objects.AddressObject
import com.choitaek.meethere.databinding.ActivityRegisterBinding
import com.choitaek.meethere.retrofit.request.Register
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    lateinit var addressObject: AddressObject

    var flag: Boolean = false

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("addressObject")) {
                        addressObject = data.getSerializableExtra("addressObject") as AddressObject
                        binding.registerAddress.setText(addressObject.road_address_name)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        //다음 단계 버튼 클릭 시
        binding.nextBUTTON.setOnClickListener {
            if(flag) {
                return@setOnClickListener
            }
            flag = true

            var EMAIL: String = binding.registerEmail.text.toString()
            var PW: String = binding.registerPw.text.toString()
            var RE_PW: String = binding.registerRewritePw.text.toString()
            var NAME: String = binding.registerName.text.toString()
            var ADDRESS: String = binding.registerAddress.text.toString()
            var PHONE: String = binding.registerPhone.text.toString()

            //회원가입 입력데이터 유효성 확인
            if (checkValidation(
                    EMAIL,
                    PW,
                    RE_PW,
                    NAME,
                    ADDRESS,
                    PHONE
                )
            ) {
                flag = false
                return@setOnClickListener
            }
            var myRegister =
                Register(
                    EMAIL, PW, RE_PW, NAME, PHONE, "WWG",
                    com.choitaek.meethere.retrofit.request.AddressObject(addressObject.place_name,
                        addressObject.road_address_name,
                        addressObject.address_name,
                        addressObject.lat,
                        addressObject.lon)
                )

            //회원가입 API 호출
            RetrofitManager.instance.registerService(
                register = myRegister,
                completion = { responseState, responseBody ->
                    when (responseState) {
                        //API 호출 성공시
                        RESPONSE_STATE.OKAY -> {

                            Log.d(TAG, "API 호출 성공 : $responseBody")

                            //JSON parsing
                            //{}->JSONObject, []->JSONArray
                            val jsonObject = JSONObject(responseBody)
                            val statusCode = jsonObject.getInt("statusCode")

                            if (statusCode == 201) {
                                val data = jsonObject.getJSONObject("data")
                                val inputEmail = data.getString("email")
                                Log.d(TAG, "inputEmail = $inputEmail")

                                //인증 단계로 이동
                                var intent = Intent(this, AuthIDActivity::class.java)
                                intent.putExtra("inputEmail", inputEmail)
                                startActivity(intent)
                            } else {
                                binding.nextBUTTON.visibility = View.VISIBLE
                                val errorMessage = jsonObject.getString("message")
                                Log.d(TAG, "error message = $errorMessage")
                                Toast.makeText(
                                    this@RegisterActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                        //API 호출 실패시
                        RESPONSE_STATE.FAIL -> {
                            Log.d(TAG, "API 호출 실패 : $responseBody")
                            binding.nextBUTTON.visibility = View.VISIBLE
                        }
                    }
                }
            )
        }

        register_address.setOnClickListener {
            val intent = Intent(this, SearchAddressActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    //회원 가입 입력 데이터 유효성 확인
    private fun checkValidation(
        EMAIL: String,
        PW: String,
        RE_PW: String,
        NAME: String,
        ADDRESS: String,
        PHONE: String,
    ): Boolean {
        //이메일 미입력 시
        if (EMAIL == "") {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }
        //이메일 형식이 아닌 경우
        if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
            Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }

        //비밀번호 미입력 시
        if (PW == "") {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }
        //비밀번호 유효성 확인
        if (!Pattern.matches("(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{6,12}", PW)) {
            Toast.makeText(this, "비밀번호 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }

        //재입력 비밀번호 미입력 시
        if (RE_PW == "") {
            Toast.makeText(this, "확인 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }
        //재입력 비밀번호 일치 여부 확인
        if (PW != RE_PW) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }

        //이름 미입력 시
        //비밀번호 미입력 시
        if (NAME == "") {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }
        //주소 미입력 시
        //비밀번호 미입력 시
        if (ADDRESS == "") {
            Toast.makeText(this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
            return true
        }

        //핸드폰 번호 유효성 확인
        if (!Pattern.matches("^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$", PHONE)) {
            Toast.makeText(this, "올바른 휴대폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show()
            binding.nextBUTTON.visibility = View.VISIBLE
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