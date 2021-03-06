package com.choitaek.meethere.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.choitaek.meethere.fragment.ViewPagerFragmentBus
import com.choitaek.meethere.fragment.ViewPagerFragmentBusSubway
import com.choitaek.meethere.fragment.ViewPagerFragmentSubway
import com.choitaek.meethere.objects.ItemComponent
import com.choitaek.meethere.objects.RouteItemComponent

class ViewPagerAdapter(fragmentActivity: FragmentActivity, wholedetailroutelist : List<List<RouteItemComponent>>, wholeroutelist : List<ItemComponent>)
    : FragmentStateAdapter(fragmentActivity){
    private val routelist = wholeroutelist
    private val detailroutelist = wholedetailroutelist
    private val TYPE_BUS = 0
    private val TYPE_SUBWAY = 1
    private val TYPE_BUS_SUBWAY = 2
    private var listPager:List<Int> = listOf(TYPE_BUS, TYPE_SUBWAY, TYPE_BUS_SUBWAY)

    private var bus_route : MutableList<ItemComponent> = arrayListOf()
    private var subway_route : MutableList<ItemComponent> = arrayListOf()
    private var bus_subway_route : MutableList<ItemComponent> = arrayListOf()

    override fun getItemCount(): Int {
        return listPager.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun createFragment(position: Int): Fragment {
        //fragment 생성 시 일단은 그대로 하는데 출발지에서 목적지로 가는 정보에서 버스만에 대한 경로
        // 지하철에 대한 경로 , 지하철+버스 경로를 가공해서 넘길 때 인자로 넘기면 되지 않나? 해서 파라미터로 받고.

        bus_route.clear()
        subway_route.clear()
        bus_subway_route.clear()

        for(i in 0 until routelist.size){
            if(routelist[i].pathType == 1) subway_route.add(routelist[i])
            // 1번은 지하철
            else if(routelist[i].pathType == 2) bus_route.add(routelist[i])
            else bus_subway_route.add(routelist[i])
        }

        return when(position){
            TYPE_BUS -> ViewPagerFragmentBus(bus_route, detailroutelist)
            TYPE_SUBWAY -> ViewPagerFragmentSubway(subway_route, detailroutelist)
            else -> ViewPagerFragmentBusSubway(bus_subway_route, detailroutelist)
        }
    }
}