package com.choitaek.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TimeWalkFee(val totalWalk : Int, val totalTime : Int, val payment : Int, val sx : String, val sy : String, val dx : String, val dy : String) : Parcelable {
}