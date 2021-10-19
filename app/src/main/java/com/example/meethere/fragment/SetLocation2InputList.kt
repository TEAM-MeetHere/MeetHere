package com.example.meethere.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.Address
import com.example.meethere.R
import com.example.meethere.adapter.AddressAdapter
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

    val address_DataArray : ArrayList<Address> = ArrayList()
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_set_location2_input_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressAdapter = AddressAdapter(mutableListOf())
        recyclerViewAddress.adapter = addressAdapter
        recyclerViewAddress.layoutManager = LinearLayoutManager(requireContext())

        var arrayAD = arrayOf<String>(
            "인천광역시 미추홀구 인하로 100",
            "인천광역시 남동구 정각로 29",
            "인천광역시 서구 가좌동 217",
            "인천광역시 미추홀구 주안동 24-24"
        )
        var arrayNM = arrayOf<String>("김철수", "박민수", "최진수", "이상수")
        var arrayOB = Array(20) { Address("임시 주소", "임시 이름") }

        for (a in 0..3) {
            arrayOB[a] = Address(arrayAD[a], arrayNM[a])
        }

        var listnumber = 0
        btnAdd.setOnClickListener {
            /* val address = textViewAddress.text.toString()
             val name = textViewName.text.toString()*/
            val address = "주소 추가"
            val name = "임시"
            val addressObject = Address(address, name)

            addressAdapter.addAddress(arrayOB[listnumber])
            listnumber++
        }
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
        @JvmStatic fun newInstance(param1: String, param2: String) =
                SetLocation2InputList().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}