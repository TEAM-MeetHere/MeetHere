package com.example.meethere.objects

import java.io.Serializable

data class BookmarkObject(
    val promise_id: Long,
    val promise_name: String,
//  val promise_member: String, // 나중에 추가
    val promise_date: String,
    val promise_place_name: String,
    val promise_road_address_name: String,
    val promise_address_name: String,
    val promise_lat: String,
    val promise_lon: String
) : Serializable