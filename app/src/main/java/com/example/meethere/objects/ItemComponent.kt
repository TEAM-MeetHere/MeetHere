package com.example.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ItemComponent(val pathType : Int, val totalTime : Int, val walkTime : Int, val totalFee : Int, val routesInfo : MutableList<String>, val totalTimeTable : MutableList<Pair<Int, Int>>) : Parcelable