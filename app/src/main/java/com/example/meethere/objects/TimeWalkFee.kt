package com.example.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TimeWalkFee(val totalWalk : Int, val totalTime : Int, val payment : Int) : Parcelable {
}