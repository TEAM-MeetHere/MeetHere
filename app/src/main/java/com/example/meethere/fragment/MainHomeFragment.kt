package com.example.meethere.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.meethere.R
import com.example.meethere.activity.SetLocationActivity
import com.example.meethere.activity.ShowResultActivity
import com.example.meethere.databinding.FragmentMainHomeBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.sharedpreferences.App
import com.example.meethere.utils.Constants
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.fragment_main_home.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        binding.tvMainHello.text = "안녕하세요 " + App.prefs.username + " 님"

        binding.newBtn.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.activity_new, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()
            val okButton = mDialogView.findViewById<Button>(R.id.activity_new_btn)
            okButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        binding.codeBtn.setOnClickListener {
            val mCodeView =
                LayoutInflater.from(requireContext()).inflate(R.layout.activity_input_code, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mCodeView)

            val mAlertDialog = mBuilder.show()
            val okButton = mCodeView.findViewById<Button>(R.id.input_code_btn)
            okButton.setOnClickListener {

                val random_code =
                    mCodeView.findViewById<EditText>(R.id.input_code_text).text.toString()
                //공유코드(도착지점) 불러오기 API 호출
                RetrofitManager.instance.shareDestinationService(
                    code = random_code,
                    completion = { responseState, responseBody ->
                        when (responseState) {
                            //API 호출 성공시
                            RESPONSE_STATE.OKAY -> {
                                //JSON parsing
                                //{}->JSONObject, []->JSONArray
                                val jsonObject = JSONObject(responseBody)
                                val statusCode = jsonObject.getInt("statusCode")

                                if (statusCode == 200) {
                                    val message = jsonObject.getString("message")
                                    val data = jsonObject.getJSONObject("data")

                                    val shareId = data.getLong("id")
                                    val placeName = data.getString("placeName")
                                    val username = data.getString("username")
                                    val roadAddressName = data.getString("roadAddressName")
                                    val addressName = data.getString("addressName")
                                    val lat = data.getDouble("lat")
                                    val lon = data.getDouble("lon")

                                    var addressObject = AddressObject(placeName,
                                        username,
                                        roadAddressName,
                                        addressName,
                                        lat,
                                        lon)
                                    var addressObjects = ArrayList<AddressObject>()

                                    //공유코드(출발지점 리스트) 불러오기 API 호출
                                    RetrofitManager.instance.shareStartService(
                                        shareId = shareId,
                                        completion = { responseState, responseBody ->
                                            when (responseState) {
                                                //API 호출 성공시
                                                RESPONSE_STATE.OKAY -> {
                                                    Log.d(Constants.TAG,
                                                        "API 호출 성공 : $responseBody")

                                                    //JSON parsing
                                                    //{}->JSONObject, []->JSONArray
                                                    val jsonObject = JSONObject(responseBody)
                                                    val statusCode = jsonObject.getInt("statusCode")

                                                    if (statusCode == 200) {
                                                        val dataArray =
                                                            jsonObject.getJSONArray("data")
                                                        for (i in 0..dataArray.length() - 1) {

                                                            val iObject = dataArray.getJSONObject(i)

                                                            val placeName =
                                                                iObject.getString("placeName")
                                                            val username =
                                                                iObject.getString("username")
                                                            val roadAddressName =
                                                                iObject.getString("roadAddressName")
                                                            val addressName =
                                                                iObject.getString("addressName")
                                                            val lat = iObject.getDouble("lat")
                                                            val lon = iObject.getDouble("lon")

                                                            addressObjects.add(AddressObject(
                                                                placeName,
                                                                username,
                                                                roadAddressName,
                                                                addressName,
                                                                lat,
                                                                lon))
                                                        }

                                                        val intent = Intent(requireContext(),
                                                            ShowResultActivity::class.java)
                                                        intent.putExtra("addressData",
                                                            addressObjects.toArray(arrayOfNulls<AddressObject>(
                                                                addressObjects.size)))
                                                        intent.putExtra("addressObject",
                                                            addressObject)
                                                        startActivity(intent)
                                                        mAlertDialog.dismiss()

                                                    } else {
                                                        var errorMessage =
                                                            jsonObject.getString("message")
                                                        Log.d(Constants.TAG,
                                                            "error message = $errorMessage")
                                                        Toast.makeText(requireContext(),
                                                            errorMessage,
                                                            Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                                //API 호출 실패시
                                                RESPONSE_STATE.FAIL -> {
                                                    Log.d(Constants.TAG,
                                                        "API 호출 실패 : $responseBody")
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    val errorMessage = jsonObject.getString("message")
                                    Log.d(Constants.TAG, "error message = $errorMessage")
                                    Toast.makeText(requireContext(),
                                        errorMessage,
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                            //API 호출 실패시
                            RESPONSE_STATE.FAIL -> {
                                Log.d(Constants.TAG, "API 호출 실패 : $responseBody")
                            }
                        }
                    }
                )
            }
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(requireContext(), SetLocationActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}