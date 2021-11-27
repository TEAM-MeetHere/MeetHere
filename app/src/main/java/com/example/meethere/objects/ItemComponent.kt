package com.example.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ItemComponent(val resultIndex : Int, var pathType : Int, var totalTime : Int, var walkTime : Int, val totalFee : Int, val routesInfo : MutableList<String>, val totalTimeTable : MutableList<Pair<Int, Int>>) : Parcelable