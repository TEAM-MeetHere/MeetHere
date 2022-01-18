package com.choitaek.meethere.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_TOKEN = "token"
    val PREF_MEMBERID = "memberId"
    val PREF_EMAIL = "email"
    val PREF_PW = "pw"
    val PREF_NAME = "name"
    val PREF_PHONE = "phone"
    val PREF_PLACE_NAME = "place_name"
    val PREF_ROAD_ADDRESS_NAME = "road_address_name"
    val PREF_ADDRESS_NAME = "address_name"
    val PREF_LAT = "lat"
    val PREF_LON = "lon"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    //파일 이름과 token을 저장할 key 값을 만들고 prefs 인스턴스 초기화

    var token: String?
        get() = prefs.getString(PREF_KEY_TOKEN, "")
        set(value) = prefs.edit().putString(PREF_KEY_TOKEN, value).apply()
    //get,set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
    //set(value) 실행 시, value로 값을 대체한 후 저장

    var memberId: Long?
        get() = prefs.getLong(PREF_MEMBERID, 0)
        set(value) = prefs.edit().putLong(PREF_MEMBERID, value!!).apply()

    var email: String?
        get() = prefs.getString(PREF_EMAIL, "")
        set(value) = prefs.edit().putString(PREF_EMAIL, value).apply()

    var pw: String?
        get() = prefs.getString(PREF_PW, "")
        set(value) = prefs.edit().putString(PREF_PW, value).apply()


    var username: String?
        get() = prefs.getString(PREF_NAME, "")
        set(value) = prefs.edit().putString(PREF_NAME, value).apply()

    var phone: String?
        get() = prefs.getString(PREF_PHONE, "")
        set(value) = prefs.edit().putString(PREF_PHONE, value).apply()

    var place_name: String?
        get() = prefs.getString(PREF_PLACE_NAME, "")
        set(value) = prefs.edit().putString(PREF_PLACE_NAME, value).apply()

    var road_address_name
        get() = prefs.getString(PREF_ROAD_ADDRESS_NAME, "")
        set(value) = prefs.edit().putString(PREF_ROAD_ADDRESS_NAME, value).apply()

    var address_name
        get() = prefs.getString(PREF_ADDRESS_NAME, "")
        set(value) = prefs.edit().putString(PREF_ADDRESS_NAME, value).apply()

    var lat
        get() = prefs.getString(PREF_LAT, "")
        set(value) = prefs.edit().putString(PREF_LAT, value).apply()

    var lon
        get() = prefs.getString(PREF_LON, "")
        set(value) = prefs.edit().putString(PREF_LON, value).apply()
}