package com.example.meethere.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.R
import com.example.meethere.activity.OtherRouteItemDecoration
import com.example.meethere.adapter.ViewPagerRecyclerViewAdapter
import com.example.meethere.objects.ItemComponent
import com.example.meethere.objects.RouteItemComponent

class ViewPagerFragmentSubway(var routelist : MutableList<ItemComponent>, wholedetailroutelist : List<List<RouteItemComponent>>) : Fragment() {
    private lateinit var recyclerView : RecyclerView
    val detailroutelist = wholedetailroutelist
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(routelist.size == 0) return inflater.inflate(R.layout.activity_viewpager_nosearch, container, false)
        else return inflater.inflate(R.layout.activity_viewpager_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("버스프래그먼트 onviewcreated호출", "호출")
        if(routelist.size != 0) {
            recyclerView = view.findViewById(R.id.rv)
            recyclerView.adapter = ViewPagerRecyclerViewAdapter(routelist, detailroutelist, requireActivity())
            recyclerView.addItemDecoration(OtherRouteItemDecoration(70))
        }
    }
}