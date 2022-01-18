package com.choitaek.meethere.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.choitaek.meethere.R
import kotlinx.android.synthetic.main.fragment_set_location3_input_address.*

import android.text.TextWatcher
import com.choitaek.meethere.activity.SetLocationActivity
import android.text.Editable
import com.choitaek.meethere.activity.FriendListActivity
import com.choitaek.meethere.objects.AddressObject
import com.choitaek.meethere.activity.SearchAddressActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SetLocation3InputAddress.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetLocation3InputAddress : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val place_name: String = ""
    val user_name: String = ""
    val road_address_name: String = ""
    val address_name: String = ""
    val lat: Double = 0.0
    val lon: Double = 0.0

    lateinit var addressObject: AddressObject

    // 웹 뷰로 이동하여 주소를 검색하고 에딧박스에 주소를 저장
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("addressObject")) {
                        addressObject = data.getSerializableExtra("addressObject") as AddressObject
                        etRoadAddressName.text = addressObject.road_address_name
                    }
                }
            }
        }

    var resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("addressObject")) {
                        addressObject = data.getSerializableExtra("addressObject") as AddressObject
                        etRoadAddressName.text = addressObject.road_address_name
                        etUserName.setText(addressObject.user_name)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_location3_input_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음엔 확인 버튼 비활성
        btnAddAddress.visibility = View.GONE

        //친구 목록으로 이동
        btn_find_friend.setOnClickListener {
            val intent = Intent(requireContext(), FriendListActivity::class.java)
            resultLauncher2.launch(intent)
        }

        // 웹 뷰로 주소 결과를 검색하기 위하여 이동하는 함수
        etRoadAddressName.setOnClickListener {
/*            val intent = Intent(requireContext(), WebViewActivity::class.java)
            resultLauncher.launch(intent)*/
            val intent = Intent(requireContext(), SearchAddressActivity::class.java)
            resultLauncher.launch(intent)
        }

        etRoadAddressName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                // 주소가 입력되고 나면 버튼을 활성화
                if (editable.isNotEmpty()) {
                    btnAddAddress.visibility = View.VISIBLE
                } else {
                    btnAddAddress.visibility = View.GONE
                }
            }
        })

        // 입력받은 이름들로 주소를 추가하기 위해 메인 액티비티의 addAddress를 거쳐 2번 프래그먼트의 addAddress를 호출
        btnAddAddress.setOnClickListener {
            var name: String = etUserName.text.toString()

            if (name == "") {
                addressObject.user_name = "홍길동"
            }
            else {
                addressObject.user_name = name
            }

            etRoadAddressName.setText("")
            etUserName.setText("")
            (activity as SetLocationActivity).addAddress(addressObject)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SetLocation3InputAddress.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SetLocation3InputAddress().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}