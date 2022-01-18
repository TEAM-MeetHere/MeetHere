package com.choitaek.meethere.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.choitaek.meethere.fragment.ViewPagerFragmentCityBus
import com.choitaek.meethere.fragment.ViewPagerFragmentTrain
import com.choitaek.meethere.objects.ItemComponent
import com.choitaek.meethere.objects.RouteItemComponent

class ViewPagerAdapterCity(fragmentActivity: FragmentActivity, wholedetailroutelist : List<List<RouteItemComponent>>, wholeroutelist : List<ItemComponent>)
    : FragmentStateAdapter(fragmentActivity){
    private val routelist = wholeroutelist
    private val detailroutelist = wholedetailroutelist
    private val TYPE_CITY_BUS = 0
    private val TYPE_TRAIN = 1
    private val TYPE_AIR = 2
    private var listPager:List<Int> = listOf(TYPE_CITY_BUS, TYPE_TRAIN, TYPE_AIR)

    private var bus_route : MutableList<ItemComponent> = arrayListOf()
    private var bus_index : MutableList<Int> = arrayListOf()
    private var train_route : MutableList<ItemComponent> = arrayListOf()
    private var train_index : MutableList<Int> = arrayListOf()
    private var air_route : MutableList<ItemComponent> = arrayListOf()
    private var air_index : MutableList<Int> = arrayListOf()

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
        bus_index.clear()
        bus_route.clear()
        train_index.clear()
        train_route.clear()
        air_index.clear()
        air_route.clear()

        for(i in 0 until routelist.size){
            for(j in 0 until detailroutelist[i].size){
                if(detailroutelist[i][j].busNoORname == "열차"){
                    train_index.add(i)
                    train_route.add(routelist[i])
                    break
                }
                if(detailroutelist[i][j].busNoORname == "버스"){
                    bus_index.add(i)
                    bus_route.add(routelist[i])
                    break
                }
                if(detailroutelist[i][j].busNoORname == "비행기"){
                    air_index.add(i)
                    air_route.add(routelist[i])
                    break
                }
            }
        }

        return when(position){
            TYPE_CITY_BUS -> ViewPagerFragmentCityBus(bus_route, bus_index, detailroutelist)
            TYPE_TRAIN -> ViewPagerFragmentTrain(train_route, train_index, detailroutelist)
            else -> ViewPagerFragmentCityBus(air_route, air_index, detailroutelist)
        }
    }
}