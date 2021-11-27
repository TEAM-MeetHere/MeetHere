package com.example.meethere.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.R
import com.example.meethere.activity.OtherRouteItemDecoration
import com.example.meethere.adapter.ViewPagerRecyclerViewCityAdapter
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent

class ViewPagerFragmentAir(
    var air_routelist : MutableList<ItemComponent>, var air_indexlist : MutableList<Int>, wholedetailroutelist : List<List<RouteItemComponent>>)
    : Fragment() {
    private lateinit var recyclerView : RecyclerView
    val detailroutelist = wholedetailroutelist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(air_routelist.size != 0) {
            return inflater.inflate(R.layout.activity_viewpager_city, container, false)
        }
        else return inflater.inflate(R.layout.activity_viewpager_nosearch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(air_routelist.size != 0) {
            recyclerView = view.findViewById(R.id.rv_city)
            recyclerView.adapter = ViewPagerRecyclerViewCityAdapter(air_routelist,air_indexlist, detailroutelist, requireActivity())
            recyclerView.addItemDecoration(OtherRouteItemDecoration(70))
        }
    }
}