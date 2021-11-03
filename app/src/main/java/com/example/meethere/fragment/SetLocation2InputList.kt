package com.example.meethere.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.AddressObject
import com.example.meethere.R
import com.example.meethere.activity.SetLocationNew
import com.example.meethere.adapter.AddressAdapter
import com.example.meethere.sharedpreferences.App
import kotlinx.android.synthetic.main.fragment_set_location2_input_list.*
import kotlinx.android.synthetic.main.fragment_set_location2_input_list.btnAdd

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SetLocation2InputList.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetLocation2InputList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var addressAdapter: AddressAdapter

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
        return inflater.inflate(R.layout.fragment_set_location2_input_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressAdapter = AddressAdapter(mutableListOf())
        recyclerViewAddress.adapter = addressAdapter
        recyclerViewAddress.layoutManager = LinearLayoutManager(requireContext())

        var listnumber = 0
        btnAdd.setOnClickListener {
            // 주소를 입력하는 프래그먼트로 이동
            (activity as SetLocationNew).changeFragment(3)
        }

        //// 디버그 코드

        addAddress(
            AddressObject(
                App.prefs.place_name.toString(),
                App.prefs.username.toString(),
                App.prefs.road_address_name.toString(),
                App.prefs.address_name.toString(),
                App.prefs.lat!!.toDouble(),
                App.prefs.lon!!.toDouble()
            )
        )

        Log.d("임영택 SetLocation2 lat prefs",App.prefs.lat!!)
        Log.d("임영택 SetLocation2 lon prefs",App.prefs.lon!!)


        //// 나중에 회원의 정보를 가장 먼저 추가하는 부분
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SetLocation2InputList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SetLocation2InputList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // 메인에서 2번 프래그먼트에게 호출할 함수
    fun addAddress(addressObject: AddressObject) {
        addressAdapter.addAddress(addressObject)
    }

    fun getData(): MutableList<AddressObject> {
        return addressAdapter.getData()
    }

    fun getSize(): Int {
        return addressAdapter.itemCount
    }
}