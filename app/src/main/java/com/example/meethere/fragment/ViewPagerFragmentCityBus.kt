package com.example.meethere.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.R
import com.example.meethere.activity.OtherRouteItemDecoration
import com.example.meethere.adapter.ViewPagerRecyclerViewAdapter
import com.example.meethere.adapter.ViewPagerRecyclerViewCityAdapter
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent
import kotlinx.android.synthetic.main.activity_viewpager_city.*

class ViewPagerFragmentCityBus(
    var bus_routelist : MutableList<ItemComponent>, var bus_indexlist : MutableList<Int>, wholedetailroutelist : List<List<RouteItemComponent>>)
    : Fragment() {
    private lateinit var recyclerView : RecyclerView
    val detailroutelist = wholedetailroutelist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(bus_routelist.size != 0) {
            return inflater.inflate(R.layout.activity_viewpager_city, container, false)
        }
        else return inflater.inflate(R.layout.activity_viewpager_nosearch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(bus_routelist.size != 0) {
            recyclerView = view.findViewById(R.id.rv_city)
            recyclerView.adapter = ViewPagerRecyclerViewCityAdapter(bus_routelist,bus_indexlist, detailroutelist, requireActivity())
            recyclerView.addItemDecoration(OtherRouteItemDecoration(70))
        }
    }
}