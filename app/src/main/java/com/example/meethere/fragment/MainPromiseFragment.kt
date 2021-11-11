package com.example.meethere.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meethere.R
import com.example.meethere.databinding.FragmentMainPromiseBinding
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainPromiseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPromiseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val t1: Fragment = MainPromiseListFragment()
    private var t2: Fragment = MainPromiseCalendarFragment()

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
        val binding = FragmentMainPromiseBinding.inflate(inflater, container, false)
        childFragmentManager.beginTransaction().add(binding.FrameLayoutPromise.id, t1, "TAG1")
        childFragmentManager.beginTransaction().add(binding.FrameLayoutPromise.id, t2, "TAG2")

        fun replaceView(fragment: Fragment) {
            childFragmentManager.beginTransaction().apply {
                if (fragment.isAdded) {
                    show(fragment)
                } else {
                    add(binding.FrameLayoutPromise.id, fragment)
                }

                childFragmentManager.fragments.forEach {
                    if (it != fragment && it.isAdded) {
                        hide(it)
                    }
                }
            }.commit()
        }

        replaceView(t1)

        binding.tabLayoutPromise.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0-> {
                        replaceView(t1)
                    }
                    1-> {
                        replaceView(t2)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainPromiseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainPromiseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}