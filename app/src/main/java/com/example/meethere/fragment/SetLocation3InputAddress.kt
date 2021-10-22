package com.example.meethere.fragment

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.meethere.R
import kotlinx.android.synthetic.main.fragment_set_location3_input_address.*
import androidx.core.app.ActivityCompat.startActivityForResult

import com.example.meethere.activity.WebViewActivity
import android.R.attr.data
import com.example.meethere.activity.SetLocationNew


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

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("data")) {
                        etAddress.setText(data.getStringExtra("data"))
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

        etAddress.setOnClickListener {
/*
            val i = Intent(requireContext(), WebViewActivity::class.java)
            startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY)
*/
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            resultLauncher.launch(intent)
        }

        btnAddAddress.setOnClickListener {
            val address: String = etAddress.text.toString()
            val name: String = etName.text.toString()
            etAddress.setText("")
            etName.setText("")
            (activity as SetLocationNew).addAddress(address, name)
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