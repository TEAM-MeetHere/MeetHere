package com.choitaek.meethere.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SourceDestination(val sx : Double, val sy : Double, val dx : Double, val dy: Double) : Parcelable{
}